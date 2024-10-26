TRUNCATE TABLE payment_method RESTART IDENTITY CASCADE;

INSERT INTO payment_method (name, transaction_fee)
VALUES ('Cash', 0.10),
       ('Credit card', 0.25),
       ('Check', 0.00);