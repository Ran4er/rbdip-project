-- Contract after the new application has switched reads/writes to normalized
-- columns. At this point the dual-write triggers have kept both models synced.
ALTER TABLE orders ALTER COLUMN customer_id SET NOT NULL;
ALTER TABLE order_items ALTER COLUMN product_id SET NOT NULL;

DROP TRIGGER trg_sync_order_item_product_columns ON order_items;
DROP FUNCTION sync_order_item_product_columns();

DROP TRIGGER trg_sync_order_customer_columns ON orders;
DROP FUNCTION sync_order_customer_columns();

DROP TRIGGER trg_sync_customer_name_columns ON customers;
DROP FUNCTION sync_customer_name_columns();

ALTER TABLE order_items
    DROP COLUMN product_name,
    DROP COLUMN product_price;

ALTER TABLE orders
    DROP COLUMN customer_full_name,
    DROP COLUMN customer_address,
    DROP COLUMN customer_phone;

ALTER TABLE customers
    DROP COLUMN customer_full_name;
