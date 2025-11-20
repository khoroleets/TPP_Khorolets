-- Видаляємо старі таблиці, якщо вони існують
DROP TABLE IF EXISTS offer;
DROP TABLE IF EXISTS country;
DROP TABLE IF EXISTS travel_type;
DROP TABLE IF EXISTS users;

-- Створюємо Таблиці
CREATE TABLE travel_type (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE country (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  iso_code CHAR(2),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE offer (
  id SERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  travel_type_id INT NOT NULL REFERENCES travel_type(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  country_id INT NOT NULL REFERENCES country(id) ON DELETE RESTRICT ON UPDATE CASCADE,
  price NUMERIC(10,2) NOT NULL,
  start_date DATE,
  end_date DATE,
  seats INT DEFAULT 0,
  available BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (username, password, role) VALUES
('admin', 'YWRtaW5wYXNz', 'ROLE_ADMIN');

INSERT INTO users (username, password, role) VALUES
('user', 'dXNlcnBhc3M=', 'ROLE_USER');

-- Наповнюємо даними (Initial data)
INSERT INTO travel_type (name, description) VALUES
('Пляжний','Відпочинок на морі'),
('Екскурсійний','Огляди пам''яток'), 
('Гірський','Гірські походи');

INSERT INTO country (name, iso_code) VALUES
('Україна','UA'),
('Польща','PL'),
('Іспанія','ES');

INSERT INTO offer (title, description, travel_type_id,
country_id, price, start_date, end_date, seats) VALUES
('Сонячний берег 7 днів','Все включено',1,3,599.00,'2026-06-01','2026-06-08',30),
('Львів і замки','Екскурсійний тур Львовом',2,1,120.00,'2025-11-10','2025-11-12',20),
('Трекінг у Карпатах','5 днів з інструктором',3,1,250.00,'2026-07-05','2026-07-10',15);