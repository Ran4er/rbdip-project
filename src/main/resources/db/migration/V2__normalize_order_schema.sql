-- Expand the normalized model while keeping every V1 column available.
-- New foreign keys stay nullable until the final contract step so a V1
-- application can continue writing rows during this migration.
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_full_name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(50)
);

ALTER TABLE orders ADD COLUMN customer_id BIGINT;
ALTER TABLE order_items ADD COLUMN product_id BIGINT;

INSERT INTO customers (customer_full_name, address, phone)
SELECT DISTINCT customer_full_name, customer_address, customer_phone
FROM orders;

UPDATE orders o
SET customer_id = c.id
FROM customers c
WHERE c.customer_full_name = o.customer_full_name
  AND c.address IS NOT DISTINCT FROM o.customer_address
  AND c.phone IS NOT DISTINCT FROM o.customer_phone;

UPDATE order_items oi
SET product_id = p.id
FROM products p
WHERE p.name = oi.product_name
  AND p.price = oi.product_price;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products(id);
