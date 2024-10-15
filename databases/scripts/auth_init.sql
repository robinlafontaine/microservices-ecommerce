CREATE TABLE IF NOT EXISTS user_data(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR NOT NULL,
    last_name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,
    password_hash VARCHAR NOT NULL,
    role VARCHAR NOT NULL,
    position VARCHAR
);