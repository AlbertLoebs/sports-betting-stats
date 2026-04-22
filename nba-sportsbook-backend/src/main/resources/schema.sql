-- users table: stores account + balance (in cents)
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    balance_cents INTEGER NOT NULL DEFAULT 0     -- balance is stored as cents to avoid miscalc due to binary
);

-- bets table
CREATE TABLE IF NOT EXISTS bets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    wager_cents INTEGER NOT NULL,
    combined_odds INTEGER NOT NULL,
    potential_payout_cents INTEGER NOT NULL,
    status TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- each selection in a parlay
CREATE TABLE IF NOT EXISTS bet_legs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bet_id INTEGER NOT NULL,
    game_id TEXT NOT NULL,
    selection TEXT NOT NULL,
    bet_type TEXT NOT NULL,
    line REAL,
    odds INTEGER NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (bet_id) REFERENCES bets(id)
);