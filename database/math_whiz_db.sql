-- KHALID - MATH WHIZ : database creation script
CREATE DATABASE IF NOT EXISTS math_whiz_db;
USE math_whiz_db;

CREATE TABLE IF NOT EXISTS game_scores (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    player_name VARCHAR(100) NOT NULL,
    score       INT          NOT NULL,
    difficulty  VARCHAR(20)  NOT NULL DEFAULT 'EASY',
    played_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Check saved records:
-- SELECT * FROM game_scores;

-- Top 10 leaderboard (same query the app uses):
-- SELECT * FROM game_scores ORDER BY score DESC, played_at ASC LIMIT 10;

-- Delete all old scores and restart IDs from 1:
-- TRUNCATE TABLE game_scores;
