CREATE SCHEMA IF NOT EXISTS stocks;
CREATE USER stocksuser WITH PASSWORD 'stockspassword';
GRANT ALL PRIVILEGES ON SCHEMA stocks TO stocksuser;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA stocks TO stocksuser;
ALTER USER stocksuser SET search_path = stocks;