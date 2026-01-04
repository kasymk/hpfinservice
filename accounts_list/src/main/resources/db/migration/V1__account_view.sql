CREATE TABLE account_view (
    account_id   VARCHAR(64) PRIMARY KEY,
    client_id    VARCHAR(64) NOT NULL,
    balance      NUMERIC(19,4) NOT NULL,
    currency     VARCHAR(3) NOT NULL,
    last_updated TIMESTAMP NOT NULL
);

CREATE INDEX idx_account_view_client
ON account_view (client_id);

CREATE TABLE processed_event (
    event_id VARCHAR(64) PRIMARY KEY,
    processed_at TIMESTAMP NOT NULL
);