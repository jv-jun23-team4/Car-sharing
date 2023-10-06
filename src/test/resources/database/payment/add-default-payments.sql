-- Insert cars
INSERT INTO cars (id, model, brand, type, inventory, daily_fee, is_deleted) VALUES
(1, 'ModelX', 'Tesla', 'SEDAN', 10, 100.00, false);

-- Insert users
INSERT INTO users (id, email, password, first_name, last_name, role, chat_id, is_deleted) VALUES
(1, 'user1@email.com', 'password1', 'Alice', 'Smith', 'CUSTOMER', 1234567890, false);

-- Insert rentals
INSERT INTO rentals (id, rental_date, return_date, car_id, user_id) VALUES
(1, CURDATE(), CURDATE() + INTERVAL 7 DAY, 1, 1);

-- Insert cars
INSERT INTO payments (id, status, type, rental_id, session_url, session_id, amount_to_pay, expired_time) VALUES
(1, 'PENDING', 'PAYMENT', 1, 'https://sample.url/session1', 'session1', 50.00, NOW());
