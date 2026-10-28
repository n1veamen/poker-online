CREATE TABLE IF NOT EXISTS hands (
    id BIGSERIAL PRIMARY KEY,
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    pot_total NUMERIC(19,2),
    board_cards TEXT,
    winner_nickname VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS hand_players (
    id BIGSERIAL PRIMARY KEY,
    hand_id BIGINT NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    starting_stack NUMERIC(19,2),
    final_stack NUMERIC(19,2),
    hole_cards TEXT,
    folded BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_hand_players_hand FOREIGN KEY (hand_id) REFERENCES hands(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS actions (
    id BIGSERIAL PRIMARY KEY,
    hand_id BIGINT NOT NULL,
    nickname VARCHAR(255) NOT NULL,
    street VARCHAR(50) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    amount NUMERIC(19,2),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_actions_hand FOREIGN KEY (hand_id) REFERENCES hands(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_hands_started_at ON hands(started_at);
CREATE INDEX IF NOT EXISTS idx_hand_players_hand_id ON hand_players(hand_id);
CREATE INDEX IF NOT EXISTS idx_actions_hand_id ON actions(hand_id);
