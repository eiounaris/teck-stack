USE mall_lite_order;

INSERT INTO mall_product (name, description, price, stock, status)
VALUES
    ('Mechanical Keyboard', 'Entry product for order flow testing.', 299.00, 100, 1),
    ('Wireless Mouse', 'Seed product for pagination and stock tests.', 129.00, 200, 1),
    ('USB-C Hub', 'Seed product for cache and order tests.', 199.00, 80, 1)
ON DUPLICATE KEY UPDATE
    name = VALUES(name);
