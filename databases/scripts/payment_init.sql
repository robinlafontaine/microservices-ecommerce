CREATE TABLE payment (
     id SERIAL PRIMARY KEY,
     order_id BIGINT NOT NULL,
     amount DECIMAL(19, 2) NOT NULL,
     payment_id VARCHAR(100) NOT NULL,
     status VARCHAR(50) NOT NULL,
     timestamp TIMESTAMP NOT NULL
);