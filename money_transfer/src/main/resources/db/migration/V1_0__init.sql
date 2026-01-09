CREATE TABLE account (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE ledger_entry (
  id UUID PRIMARY KEY,
  account_id UUID,
  transfer_id UUID,
  amount NUMERIC(18,2),
  created_at TIMESTAMP,
  UNIQUE (transfer_id, account_id)
);

CREATE TABLE idempotency_key (
  id UUID PRIMARY KEY,
  created_at TIMESTAMP
);

CREATE INDEX idx_ledger_account ON ledger_entry(account_id);

CREATE INDEX idx_account_client_id ON account(client_id);

CREATE TABLE outbox_event (
  id UUID PRIMARY KEY,
  aggregate_type VARCHAR(50) NOT NULL,
  aggregate_id UUID NOT NULL,
  event_type VARCHAR(50) NOT NULL,
  payload TEXT NOT NULL,
  status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  sent_at TIMESTAMP
);

CREATE INDEX idx_outbox_event_status ON outbox_event(status);

CREATE INDEX idx_outbox_event_created_at ON outbox_event(created_at);