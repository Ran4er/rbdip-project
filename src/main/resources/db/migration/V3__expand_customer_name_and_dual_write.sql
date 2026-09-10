-- Expand customer_full_name into first_name/last_name. Nothing legacy is
-- removed here: this is the compatibility window for old and new binaries.
ALTER TABLE customers
    ADD COLUMN first_name VARCHAR(255),
    ADD COLUMN last_name VARCHAR(255);

UPDATE customers
SET first_name = split_part(trim(customer_full_name), ' ', 1),
    last_name = CASE
        WHEN position(' ' IN trim(customer_full_name)) > 0
            THEN trim(substr(trim(customer_full_name), position(' ' IN trim(customer_full_name)) + 1))
        ELSE ''
    END;

-- Catch rows that an old binary could have written after V2 started.
INSERT INTO customers (customer_full_name, address, phone, first_name, last_name)
SELECT DISTINCT
       o.customer_full_name,
       o.customer_address,
       o.customer_phone,
       split_part(trim(o.customer_full_name), ' ', 1),
       CASE
           WHEN position(' ' IN trim(o.customer_full_name)) > 0
               THEN trim(substr(trim(o.customer_full_name), position(' ' IN trim(o.customer_full_name)) + 1))
           ELSE ''
       END
FROM orders o
WHERE o.customer_id IS NULL
  AND NOT EXISTS (
      SELECT 1
      FROM customers c
      WHERE c.customer_full_name = o.customer_full_name
        AND c.address IS NOT DISTINCT FROM o.customer_address
        AND c.phone IS NOT DISTINCT FROM o.customer_phone
  );

UPDATE orders o
SET customer_id = c.id
FROM customers c
WHERE o.customer_id IS NULL
  AND c.customer_full_name = o.customer_full_name
  AND c.address IS NOT DISTINCT FROM o.customer_address
  AND c.phone IS NOT DISTINCT FROM o.customer_phone;

UPDATE order_items oi
SET product_id = p.id
FROM products p
WHERE oi.product_id IS NULL
  AND p.name = oi.product_name
  AND p.price = oi.product_price;

ALTER TABLE customers
    ALTER COLUMN first_name SET NOT NULL,
    ALTER COLUMN last_name SET NOT NULL;

-- A new application writes first_name/last_name while the legacy full-name
-- column is still NOT NULL. Fill the legacy representation before constraints.
CREATE OR REPLACE FUNCTION sync_customer_name_columns()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.first_name IS NULL AND NEW.customer_full_name IS NOT NULL THEN
        NEW.first_name := split_part(trim(NEW.customer_full_name), ' ', 1);
    END IF;
    IF NEW.last_name IS NULL AND NEW.customer_full_name IS NOT NULL THEN
        NEW.last_name := CASE
            WHEN position(' ' IN trim(NEW.customer_full_name)) > 0
                THEN trim(substr(trim(NEW.customer_full_name), position(' ' IN trim(NEW.customer_full_name)) + 1))
            ELSE ''
        END;
    END IF;
    IF NEW.customer_full_name IS NULL THEN
        NEW.customer_full_name := trim(concat_ws(' ', NEW.first_name, NEW.last_name));
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sync_customer_name_columns
BEFORE INSERT OR UPDATE ON customers
FOR EACH ROW EXECUTE FUNCTION sync_customer_name_columns();

-- Dual-write adapter for orders. Old binaries provide contact columns and get
-- customer_id populated; new binaries provide customer_id and get legacy
-- columns populated until the contract migration removes them.
CREATE OR REPLACE FUNCTION sync_order_customer_columns()
RETURNS TRIGGER AS $$
DECLARE
    matched_customer_id BIGINT;
    legacy_name VARCHAR(255);
    legacy_address VARCHAR(500);
    legacy_phone VARCHAR(50);
BEGIN
    IF NEW.customer_id IS NULL THEN
        SELECT c.id
        INTO matched_customer_id
        FROM customers c
        WHERE c.customer_full_name = NEW.customer_full_name
          AND c.address IS NOT DISTINCT FROM NEW.customer_address
          AND c.phone IS NOT DISTINCT FROM NEW.customer_phone
        ORDER BY c.id
        LIMIT 1;

        IF matched_customer_id IS NULL THEN
            INSERT INTO customers (customer_full_name, address, phone)
            VALUES (NEW.customer_full_name, NEW.customer_address, NEW.customer_phone)
            RETURNING id INTO matched_customer_id;
        END IF;
        NEW.customer_id := matched_customer_id;
    ELSE
        SELECT c.customer_full_name, c.address, c.phone
        INTO legacy_name, legacy_address, legacy_phone
        FROM customers c
        WHERE c.id = NEW.customer_id;

        NEW.customer_full_name := COALESCE(NEW.customer_full_name, legacy_name);
        NEW.customer_address := COALESCE(NEW.customer_address, legacy_address);
        NEW.customer_phone := COALESCE(NEW.customer_phone, legacy_phone);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sync_order_customer_columns
BEFORE INSERT OR UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION sync_order_customer_columns();

-- Same compatibility adapter for normalized order_items/products.
CREATE OR REPLACE FUNCTION sync_order_item_product_columns()
RETURNS TRIGGER AS $$
DECLARE
    matched_product_id BIGINT;
    legacy_name VARCHAR(255);
    legacy_price NUMERIC(10, 2);
BEGIN
    IF NEW.product_id IS NULL THEN
        SELECT p.id
        INTO matched_product_id
        FROM products p
        WHERE p.name = NEW.product_name
          AND p.price = NEW.product_price
        ORDER BY p.id
        LIMIT 1;
        NEW.product_id := matched_product_id;
    ELSE
        SELECT p.name, p.price
        INTO legacy_name, legacy_price
        FROM products p
        WHERE p.id = NEW.product_id;

        NEW.product_name := COALESCE(NEW.product_name, legacy_name);
        NEW.product_price := COALESCE(NEW.product_price, legacy_price);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sync_order_item_product_columns
BEFORE INSERT OR UPDATE ON order_items
FOR EACH ROW EXECUTE FUNCTION sync_order_item_product_columns();
