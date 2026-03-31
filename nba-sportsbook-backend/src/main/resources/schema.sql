-- users table: stores account + balance (in cents)
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    balance_cents INTEGER NOT NULL      -- balance is stored as cents to avoid miscalc due to binary
);

-- bets table
CREATE TABLE IF NOT EXISTS bets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    game_id TEXT NOT NULL,
    team_selected TEXT NOT NULL,
    odds INTEGER NOT NULL,
    wager_cents INTEGER NOT NULL,
    status TEXT NOT NULL    -- pending, win , lost
);