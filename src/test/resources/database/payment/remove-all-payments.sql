-- Delete payments
DELETE FROM payments WHERE id IN (1, 2, 3);

-- Delete rentals
DELETE FROM rentals WHERE id IN (1, 2, 3);

-- Delete users
DELETE FROM users WHERE id IN (1, 2, 3);

-- Delete cars
DELETE FROM cars WHERE id IN (1, 2, 3);
