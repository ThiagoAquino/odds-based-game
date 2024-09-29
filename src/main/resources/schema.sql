CREATE TABLE IF NOT EXISTS player (
                                    id SERIAL PRIMARY KEY,
                                    player_name VARCHAR(255),
                                    player_surname VARCHAR(255),
                                    username VARCHAR(255) UNIQUE,
                                    balance DECIMAL(10, 2)
    );


CREATE TABLE IF NOT EXISTS transactions (
                                        id SERIAL PRIMARY KEY,
                                        player_id BIGINT,
                                        amount DECIMAL(10, 2),
                                        transaction_type VARCHAR(50),
                                        CONSTRAINT fk_player
                                        FOREIGN KEY (player_id)
                                        REFERENCES player(id)
    );

CREATE TABLE IF NOT EXISTS bet (
                                id SERIAL PRIMARY KEY,
                                player_id BIGINT,
                                bet_amount DECIMAL(10, 2),
                                bet_number INT,
                                generated_number INT,
                                bet_result VARCHAR(50),
                                bet_winning DECIMAL(10, 2),
                                CONSTRAINT fk_player_bet
                                FOREIGN KEY (player_id)
                                REFERENCES player(id)
    );