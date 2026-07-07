INSERT INTO rate_masters (category, item_code, item_name, rate, amount, active)
VALUES
    ('BASE_PREMIUM', 'BASE', '基本保険料', NULL, 50000, TRUE),

    ('AGE', 'AGE_18_25', '18歳〜25歳', 1.600, NULL, TRUE),
    ('AGE', 'AGE_26_34', '26歳〜34歳', 1.250, NULL, TRUE),
    ('AGE', 'AGE_35_59', '35歳〜59歳', 1.000, NULL, TRUE),
    ('AGE', 'AGE_60_OVER', '60歳以上', 1.200, NULL, TRUE),

    ('LICENSE', 'GOLD', 'ゴールド', 0.900, NULL, TRUE),
    ('LICENSE', 'BLUE', 'ブルー', 1.000, NULL, TRUE),
    ('LICENSE', 'GREEN', 'グリーン', 1.100, NULL, TRUE),

    ('USAGE', 'PRIVATE', '日常・レジャー', 1.000, NULL, TRUE),
    ('USAGE', 'COMMUTE', '通勤・通学', 1.100, NULL, TRUE),
    ('USAGE', 'BUSINESS', '業務使用', 1.250, NULL, TRUE),

    ('MILEAGE', 'MILEAGE_0_5000', '0km〜5,000km', 0.950, NULL, TRUE),
    ('MILEAGE', 'MILEAGE_5001_10000', '5,001km〜10,000km', 1.000, NULL, TRUE),
    ('MILEAGE', 'MILEAGE_10001_OVER', '10,001km以上', 1.150, NULL, TRUE),

    ('DRIVER_RANGE', 'SELF', '本人限定', 0.900, NULL, TRUE),
    ('DRIVER_RANGE', 'COUPLE', '夫婦限定', 0.950, NULL, TRUE),
    ('DRIVER_RANGE', 'FAMILY', '家族限定', 1.050, NULL, TRUE),
    ('DRIVER_RANGE', 'ANYONE', '限定なし', 1.200, NULL, TRUE),

    ('GRADE', 'GRADE_1_5', '1等級〜5等級', 1.300, NULL, TRUE),
    ('GRADE', 'GRADE_6_10', '6等級〜10等級', 1.100, NULL, TRUE),
    ('GRADE', 'GRADE_11_15', '11等級〜15等級', 0.950, NULL, TRUE),
    ('GRADE', 'GRADE_16_20', '16等級〜20等級', 0.800, NULL, TRUE),

    ('ACCIDENT_TERM', 'ACCIDENT_TERM_0', '事故有係数適用期間 0年', 1.000, NULL, TRUE),
    ('ACCIDENT_TERM', 'ACCIDENT_TERM_1_OVER', '事故有係数適用期間 1年以上', 1.200, NULL, TRUE),

    ('VEHICLE_TYPE', 'KEI', '軽自動車', 0.900, NULL, TRUE),
    ('VEHICLE_TYPE', 'COMPACT', 'コンパクト', 0.950, NULL, TRUE),
    ('VEHICLE_TYPE', 'SEDAN', 'セダン', 1.000, NULL, TRUE),
    ('VEHICLE_TYPE', 'MINIVAN', 'ミニバン', 1.100, NULL, TRUE),
    ('VEHICLE_TYPE', 'SUV', 'SUV', 1.150, NULL, TRUE),

    ('VEHICLE_INSURANCE', 'FALSE', '車両保険なし', NULL, 0, TRUE),
    ('VEHICLE_INSURANCE', 'TRUE', '車両保険あり', NULL, 30000, TRUE),

    ('PROPERTY_DAMAGE_LIMIT', 'THIRTY_MILLION', '対物補償 3,000万円', NULL, 0, TRUE),
    ('PROPERTY_DAMAGE_LIMIT', 'UNLIMITED', '対物補償 無制限', NULL, 5000, TRUE),

    ('PERSONAL_INJURY_AMOUNT', 'THIRTY_MILLION', '人身傷害 3,000万円', NULL, 0, TRUE),
    ('PERSONAL_INJURY_AMOUNT', 'FIFTY_MILLION', '人身傷害 5,000万円', NULL, 3000, TRUE),
    ('PERSONAL_INJURY_AMOUNT', 'UNLIMITED', '人身傷害 無制限', NULL, 7000, TRUE),

    ('LAWYER_OPTION', 'FALSE', '弁護士特約なし', NULL, 0, TRUE),
    ('LAWYER_OPTION', 'TRUE', '弁護士特約あり', NULL, 2000, TRUE),

    ('ROAD_SERVICE', 'FALSE', 'ロードサービスなし', NULL, 0, TRUE),
    ('ROAD_SERVICE', 'TRUE', 'ロードサービスあり', NULL, 1500, TRUE)
ON CONFLICT (category, item_code) DO UPDATE
SET
    item_name = EXCLUDED.item_name,
    rate = EXCLUDED.rate,
    amount = EXCLUDED.amount,
    active = EXCLUDED.active;

INSERT INTO admin_users (login_id, password_hash, display_name, active)
VALUES
    ('admin', crypt('Admin123!', gen_salt('bf', 10)), '管理者', TRUE)
ON CONFLICT (login_id) DO NOTHING;
