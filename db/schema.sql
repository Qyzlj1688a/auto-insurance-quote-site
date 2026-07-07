CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS quotes (
    id BIGSERIAL PRIMARY KEY,
    quote_no VARCHAR(20) NOT NULL,
    driver_age INTEGER NOT NULL,
    license_color VARCHAR(20) NOT NULL,
    usage_type VARCHAR(20) NOT NULL,
    annual_mileage INTEGER NOT NULL,
    driver_range VARCHAR(20) NOT NULL,
    has_current_insurance BOOLEAN NOT NULL,
    grade INTEGER,
    accident_term INTEGER,
    maker VARCHAR(50) NOT NULL,
    car_name VARCHAR(50) NOT NULL,
    first_registration_ym CHAR(7) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    vehicle_insurance BOOLEAN NOT NULL,
    property_damage_limit VARCHAR(20) NOT NULL,
    personal_injury_amount VARCHAR(20) NOT NULL,
    lawyer_option BOOLEAN NOT NULL,
    road_service BOOLEAN NOT NULL,
    annual_premium INTEGER NOT NULL,
    monthly_premium INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_quotes_quote_no UNIQUE (quote_no),
    CONSTRAINT chk_quotes_driver_age CHECK (driver_age BETWEEN 18 AND 100),
    CONSTRAINT chk_quotes_license_color CHECK (license_color IN ('GOLD', 'BLUE', 'GREEN')),
    CONSTRAINT chk_quotes_usage_type CHECK (usage_type IN ('PRIVATE', 'COMMUTE', 'BUSINESS')),
    CONSTRAINT chk_quotes_annual_mileage CHECK (annual_mileage BETWEEN 0 AND 30000),
    CONSTRAINT chk_quotes_driver_range CHECK (driver_range IN ('SELF', 'COUPLE', 'FAMILY', 'ANYONE')),
    CONSTRAINT chk_quotes_grade CHECK (grade IS NULL OR grade BETWEEN 1 AND 20),
    CONSTRAINT chk_quotes_accident_term CHECK (accident_term IS NULL OR accident_term BETWEEN 0 AND 6),
    CONSTRAINT chk_quotes_current_insurance_required CHECK (
        has_current_insurance = FALSE OR (grade IS NOT NULL AND accident_term IS NOT NULL)
    ),
    CONSTRAINT chk_quotes_maker_not_blank CHECK (char_length(trim(maker)) > 0),
    CONSTRAINT chk_quotes_car_name_not_blank CHECK (char_length(trim(car_name)) > 0),
    CONSTRAINT chk_quotes_first_registration_ym CHECK (
        first_registration_ym ~ '^[0-9]{4}-(0[1-9]|1[0-2])$'
    ),
    CONSTRAINT chk_quotes_vehicle_type CHECK (vehicle_type IN ('COMPACT', 'SEDAN', 'MINIVAN', 'SUV', 'KEI')),
    CONSTRAINT chk_quotes_property_damage_limit CHECK (
        property_damage_limit IN ('UNLIMITED', 'THIRTY_MILLION')
    ),
    CONSTRAINT chk_quotes_personal_injury_amount CHECK (
        personal_injury_amount IN ('THIRTY_MILLION', 'FIFTY_MILLION', 'UNLIMITED')
    ),
    CONSTRAINT chk_quotes_premiums_non_negative CHECK (
        annual_premium >= 0 AND monthly_premium >= 0
    )
);

CREATE TABLE IF NOT EXISTS quote_breakdowns (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    rate NUMERIC(6,3),
    amount INTEGER,
    display_order INTEGER NOT NULL,
    CONSTRAINT fk_quote_breakdowns_quote_id
        FOREIGN KEY (quote_id)
        REFERENCES quotes (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_quote_breakdowns_item_name_not_blank CHECK (char_length(trim(item_name)) > 0),
    CONSTRAINT chk_quote_breakdowns_value_exists CHECK (rate IS NOT NULL OR amount IS NOT NULL),
    CONSTRAINT chk_quote_breakdowns_display_order CHECK (display_order > 0)
);

CREATE TABLE IF NOT EXISTS rate_masters (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    rate NUMERIC(6,3),
    amount INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_rate_masters_category_item_code UNIQUE (category, item_code),
    CONSTRAINT chk_rate_masters_item_name_not_blank CHECK (char_length(trim(item_name)) > 0),
    CONSTRAINT chk_rate_masters_value_exists CHECK (rate IS NOT NULL OR amount IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS admin_users (
    id BIGSERIAL PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_admin_users_login_id UNIQUE (login_id),
    CONSTRAINT chk_admin_users_login_id_not_blank CHECK (char_length(trim(login_id)) > 0),
    CONSTRAINT chk_admin_users_display_name_not_blank CHECK (char_length(trim(display_name)) > 0)
);


CREATE INDEX IF NOT EXISTS idx_quotes_created_at
    ON quotes (created_at DESC);

CREATE INDEX IF NOT EXISTS idx_quote_breakdowns_quote_id
    ON quote_breakdowns (quote_id);

CREATE INDEX IF NOT EXISTS idx_quote_breakdowns_quote_id_display_order
    ON quote_breakdowns (quote_id, display_order);

CREATE INDEX IF NOT EXISTS idx_rate_masters_category_active
    ON rate_masters (category, active);
