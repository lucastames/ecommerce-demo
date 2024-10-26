TRUNCATE TABLE product RESTART IDENTITY CASCADE;
TRUNCATE TABLE review RESTART IDENTITY CASCADE;
TRUNCATE TABLE category RESTART IDENTITY CASCADE;
TRUNCATE TABLE product_category;

INSERT INTO product (price, stock_quantity, description, name)
VALUES (12.50, 10, 'Desc 1', 'Product 1'),
       (3.20, 20, 'Desc 2', 'Product 2'),
       (5.99, 10, 'Desc 3', 'Product 3');

INSERT INTO review (date, product_id, description, rating)
VALUES ('2024-08-27', 1, 'Product 1 is excellent 1', 'EXCELLENT'),
       ('2024-08-27', 1, 'Product 1 is bad 1', 'BAD'),
       ('2024-08-27', 1, 'Product 1 is bad 2', 'BAD'),
       ('2024-08-27', 2, 'Product 2 is good 2', 'GOOD'),
       ('2024-08-02', 2, 'Product 2 is excellent 1', 'EXCELLENT');

INSERT INTO category (name)
VALUES ('Category 1'),
       ('Category 2'),
       ('Category 3');

INSERT INTO product_category
VALUES (1, 1),
       (2, 1),
       (3, 1),
       (2, 2),
       (3, 2),
       (1, 3);
