-- insert a demo user if it does not exist, prevents dups
INSERT INTO users (username, password_hash, balance_cents)
SELECT 'Demo', 'test123', 10000
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username='Demo'
);