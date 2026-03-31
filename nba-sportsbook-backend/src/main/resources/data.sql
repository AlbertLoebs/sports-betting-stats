-- insert a demo user if it does not exist, prevents dups
INSERT INTO users (username, balance_cents)
SELECT 'Demo', 10000
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username='Demo'
);