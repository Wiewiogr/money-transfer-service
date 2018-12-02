CREATE TABLE money_transfer (
  id UUID,
  from_account UUID,
  to_account UUID,
  amount DECIMAL,
  title VARCHAR(255),
  time TIMESTAMP
);