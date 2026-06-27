DROP INDEX IF EXISTS idx_detects_time;

ALTER TABLE bank_app_detects DROP COLUMN IF EXISTS app_name;
ALTER TABLE bank_app_detects DROP COLUMN IF EXISTS session_id;
ALTER TABLE bank_app_detects DROP COLUMN IF EXISTS detected_time;

ALTER TABLE bank_app_detects ADD COLUMN app_package_name VARCHAR(100) NOT NULL;
ALTER TABLE bank_app_detects ADD COLUMN bank_name VARCHAR(50) NOT NULL;
ALTER TABLE bank_app_detects ADD COLUMN detected_at TIMESTAMP NOT NULL;

CREATE INDEX idx_detects_time ON bank_app_detects(detected_at);
