-- Insert car if not exists
INSERT INTO cars (id, type, model, brand, inventory, daily_fee)
VALUES (1, 'SEDAN', 'q7', 'audi', 6, 50.00)
    ON DUPLICATE KEY UPDATE id=id;

-- Update car by ID
UPDATE cars SET inventory = 7, daily_fee = 55.00 WHERE id = 1;
