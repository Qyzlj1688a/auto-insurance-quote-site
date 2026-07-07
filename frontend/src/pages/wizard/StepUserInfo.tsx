import React, { useState, useEffect } from "react";
import { QuoteCreateRequest } from "../../types";

interface StepUserInfoProps {
  data: QuoteCreateRequest;
  updateData: (updates: Partial<QuoteCreateRequest>) => void;
  onNext: () => void;
  onPrev: () => void;
}

export const StepUserInfo: React.FC<StepUserInfoProps> = ({
  data,
  updateData,
  onNext,
  onPrev,
}) => {
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [touched, setTouched] = useState<Record<string, boolean>>({});

  const validateField = (name: string, value: any) => {
    let errorMsg = "";
    if (name === "driverAge") {
      if (value === "") {
        errorMsg = "必須項目です";
      } else {
        const age = Number(value);
        if (isNaN(age) || age < 18 || age > 100) {
          errorMsg = "18歳以上100歳以下で入力してください";
        }
      }
    } else if (name === "annualMileage") {
      if (value === "") {
        errorMsg = "必須項目です";
      } else {
        const mileage = Number(value);
        if (isNaN(mileage) || mileage < 0 || mileage > 30000) {
          errorMsg = "0以上30000以下で入力してください";
        }
      }
    } else if (["licenseColor", "usageType", "driverRange"].includes(name)) {
      if (!value) {
        errorMsg = "必須項目です";
      }
    }
    return errorMsg;
  };

  const handleBlur = (field: string) => {
    setTouched((prev) => ({ ...prev, [field]: true }));
    const errorMsg = validateField(field, (data as any)[field]);
    setErrors((prev) => ({ ...prev, [field]: errorMsg }));
  };

  const handleChange = (field: string, value: any) => {
    updateData({ [field]: value });
    if (touched[field]) {
      const errorMsg = validateField(field, value);
      setErrors((prev) => ({ ...prev, [field]: errorMsg }));
    }
  };

  // Check overall form validity
  const isFormValid =
    data.driverAge !== "" &&
    !validateField("driverAge", data.driverAge) &&
    data.annualMileage !== "" &&
    !validateField("annualMileage", data.annualMileage) &&
    !!data.licenseColor &&
    !!data.usageType &&
    !!data.driverRange;

  const handleNextClick = () => {
    if (isFormValid) {
      onNext();
    }
  };

  return (
    <div className="wizard-card">
      <h2 className="step-title">使用者情報</h2>
      <p className="step-desc">運転者に関する条件を入力してください。</p>

      <div className="selector-layout">
        <div className="form-fields">
          {/* Driver Age */}
          <div className="form-group">
            <label className="form-label">
              運転者年齢 <span className="required-badge">必須</span>
            </label>
            <input
              type="number"
              className="input-text"
              placeholder="例：35"
              value={data.driverAge}
              onChange={(e) => handleChange("driverAge", e.target.value === "" ? "" : Number(e.target.value))}
              onBlur={() => handleBlur("driverAge")}
            />
            {touched.driverAge && errors.driverAge && (
              <span className="field-error-msg">{errors.driverAge}</span>
            )}
          </div>

          {/* License Color */}
          <div className="form-group">
            <label className="form-label">
              免許証の色 <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              {[
                { code: "GOLD", name: "ゴールド" },
                { code: "BLUE", name: "ブルー" },
                { code: "GREEN", name: "グリーン" },
              ].map((item) => (
                <div
                  key={item.code}
                  className={`radio-card ${data.licenseColor === item.code ? "active" : ""}`}
                  onClick={() => {
                    setTouched((prev) => ({ ...prev, licenseColor: true }));
                    handleChange("licenseColor", item.code);
                  }}
                >
                  <input
                    type="radio"
                    name="licenseColor"
                    checked={data.licenseColor === item.code}
                    onChange={() => {}}
                  />
                  <span>{item.name}</span>
                </div>
              ))}
            </div>
            {touched.licenseColor && !data.licenseColor && (
              <span className="field-error-msg">必須項目です</span>
            )}
          </div>

          {/* Usage Type */}
          <div className="form-group">
            <label className="form-label">
              使用目的 <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              {[
                { code: "PRIVATE", name: "日常・レジャー" },
                { code: "COMMUTE", name: "通勤・通学" },
                { code: "BUSINESS", name: "業務使用" },
              ].map((item) => (
                <div
                  key={item.code}
                  className={`radio-card ${data.usageType === item.code ? "active" : ""}`}
                  onClick={() => {
                    setTouched((prev) => ({ ...prev, usageType: true }));
                    handleChange("usageType", item.code);
                  }}
                >
                  <input
                    type="radio"
                    name="usageType"
                    checked={data.usageType === item.code}
                    onChange={() => {}}
                  />
                  <span>{item.name}</span>
                </div>
              ))}
            </div>
            {touched.usageType && !data.usageType && (
              <span className="field-error-msg">必須項目です</span>
            )}
          </div>

          {/* Annual Mileage */}
          <div className="form-group">
            <label className="form-label">
              年間走行距離 (km) <span className="required-badge">必須</span>
            </label>
            <input
              type="number"
              className="input-text"
              placeholder="例：8000"
              value={data.annualMileage}
              onChange={(e) => handleChange("annualMileage", e.target.value === "" ? "" : Number(e.target.value))}
              onBlur={() => handleBlur("annualMileage")}
            />
            {touched.annualMileage && errors.annualMileage && (
              <span className="field-error-msg">{errors.annualMileage}</span>
            )}
          </div>

          {/* Driver Range */}
          <div className="form-group">
            <label className="form-label">
              運転者範囲 <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              {[
                { code: "SELF", name: "本人限定" },
                { code: "COUPLE", name: "夫婦限定" },
                { code: "FAMILY", name: "家族限定" },
                { code: "ANYONE", name: "限定なし" },
              ].map((item) => (
                <div
                  key={item.code}
                  className={`radio-card ${data.driverRange === item.code ? "active" : ""}`}
                  onClick={() => {
                    setTouched((prev) => ({ ...prev, driverRange: true }));
                    handleChange("driverRange", item.code);
                  }}
                >
                  <input
                    type="radio"
                    name="driverRange"
                    checked={data.driverRange === item.code}
                    onChange={() => {}}
                  />
                  <span>{item.name}</span>
                </div>
              ))}
            </div>
            {touched.driverRange && !data.driverRange && (
              <span className="field-error-msg">必須項目です</span>
            )}
          </div>
        </div>

        {/* Sidebar Help */}
        <div className="help-sidebar">
          <h3>ℹ️ 条件と保費の関係</h3>
          <p style={{ marginBottom: "12px" }}>
            <strong>年齢</strong>: 若年運転者は統計上リスクが高いため、18〜25歳は最も高い割増料金（1.6倍）が適用されます。
          </p>
          <p style={{ marginBottom: "12px" }}>
            <strong>免許証の色</strong>: ゴールド免許（無事故無違反）のお客様には優良ドライバー割引（0.9倍）が適用されます。
          </p>
          <p style={{ marginBottom: "12px" }}>
            <strong>走行距離</strong>: 年間の走行距離が多いほど、事故に遭うリスクが増加するため、10,001km以上では1.15倍の割増になります。
          </p>
          <p>
            <strong>運転者範囲</strong>: 限定なし（ANYONE）など、運転できる範囲が広くなるほど保険料の割増幅が大きくなります。
          </p>
        </div>
      </div>

      <div className="actions-area">
        <button className="btn btn-secondary" onClick={onPrev}>
          戻る
        </button>
        <button className="btn btn-primary" onClick={handleNextClick} disabled={!isFormValid}>
          次へ
        </button>
      </div>
    </div>
  );
};
