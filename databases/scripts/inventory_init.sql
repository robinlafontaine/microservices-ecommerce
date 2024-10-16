CREATE TABLE product_data (
      id SERIAL PRIMARY KEY,
      product_name VARCHAR(255) NOT NULL,
      description TEXT,
      image_url VARCHAR(255),
      price NUMERIC(10, 2) NOT NULL,
      category_id INT NOT NULL,
      stock_quantity INT NOT NULL
);

CREATE TABLE category_data (
    id SERIAL PRIMARY KEY,
    category_name VARCHAR NOT NULL
);