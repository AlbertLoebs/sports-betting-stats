-- users table: stores account + balance (in cents)
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    balance_cents INTEGER NOT NULL      -- balance is stored as cents to avoid miscalc due to binary
);

-- bets table
CREATE TABLE IF NOT EXISTS bets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    wager_cents INTEGER NOT NULL,
    combined_odds INTEGER NOT NULL,
    potential_payout_cents INTEGER NOT NULL,
    status TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- each selection in a parlay
CREATE TABLE IF NOT EXISTS bet_legs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bet_id INTEGER NOT NULL,
    game_id TEXT NOT NULL,
    team TEXT NOT NULL,
    odds INTEGER NOT NULL,
    status TEXT NOT NULL,
    FOREIGN KEY (bet_id) REFERENCES bets(id)
);