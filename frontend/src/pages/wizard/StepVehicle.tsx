import React, { useState } from "react";
import { QuoteCreateRequest } from "../../types";
import { findVehicleByCode } from "../../constants/vehicleMaster";

interface StepVehicleProps {
  data: QuoteCreateRequest;
  updateData: (updates: Partial<QuoteCreateRequest>) => void;
  onNext: () => void;
  onPrev: () => void;
}

export const StepVehicle: React.FC<StepVehicleProps> = ({
  data,
  updateData,
  onNext,
  onPrev,
}) => {
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [touched, setTouched] = useState<Record<string, boolean>>({});
  const [modelSearchCode, setModelSearchCode] = useState("");
  const [searchStatus, setSearchStatus] = useState<{ success: boolean; message: string } | null>(null);

  const handleModelSearch = () => {
    setSearchStatus(null);
    if (!modelSearchCode.trim()) {
      setSearchStatus({ success: false, message: "型式を入力してください。" });
      return;
    }
    const matched = findVehicleByCode(modelSearchCode);
    if (matched) {
      updateData({
        maker: matched.maker,
        carName: matched.carName,
        vehicleType: matched.vehicleType,
      });
      // 自動入力された項目のバリデーションエラーをクリア
      setErrors((prev) => {
        const copy = { ...prev };
        delete copy.maker;
        delete copy.carName;
        delete copy.vehicleType;
        return copy;
      });
      setSearchStatus({
        success: true,
        message: `型式「${matched.modelCode}」が見つかりました。メーカー、車名、車両タイプを自動入力しました。`,
      });
    } else {
      setSearchStatus({
        success: false,
        message: "型式が見つかりません。入力内容を確認するか、手動で入力してください。",
      });
    }
  };

  const validateField = (name: string, value: any) => {
    let errorMsg = "";
    if (name === "maker") {
      if (!value || !value.trim()) {
        errorMsg = "必須項目です";
      } else if (value.length > 50) {
        errorMsg = "50文字以内で入力してください";
      }
    } else if (name === "carName") {
      if (!value || !value.trim()) {
        errorMsg = "必須項目です";
      } else if (value.length > 50) {
        errorMsg = "50文字以内で入力してください";
      }
    } else if (name === "firstRegistrationYearMonth") {
      if (!value || !value.trim()) {
        errorMsg = "必須項目です";
      } else if (!/^[0-9]{4}-(0[1-9]|1[0-2])$/.test(value)) {
        errorMsg = "YYYY-MM形式で入力してください";
      } else {
        const [year, month] = value.split("-").map(Number);
        const now = new Date();
        const curYear = now.getFullYear();
        const curMonth = now.getMonth() + 1;
        if (year > curYear || (year === curYear && month > curMonth)) {
          errorMsg = "未来の日付は入力できません";
        }
      }
    } else if (name === "vehicleType") {
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

  // フォーム全体の入力検証状態を確認
  const isFormValid =
    !!data.maker &&
    !validateField("maker", data.maker) &&
    !!data.carName &&
    !validateField("carName", data.carName) &&
    !!data.firstRegistrationYearMonth &&
    !validateField("firstRegistrationYearMonth", data.firstRegistrationYearMonth) &&
    !!data.vehicleType &&
    data.vehicleInsurance !== null;

  const handleNextClick = () => {
    if (isFormValid) {
      onNext();
    }
  };

  return (
    <div className="wizard-card">
      <h2 className="step-title">車両情報</h2>
      <p className="step-desc">車両に関する情報を入力してください。</p>

      <div className="selector-layout">
        <div className="form-fields">
          {/* 型式検索パネル */}
          <div className="form-group" style={{
            background: "var(--primary-light)",
            border: "1px dashed rgba(37, 99, 235, 0.2)",
            borderRadius: "var(--radius-md)",
            padding: "20px",
            marginBottom: "24px"
          }}>
            <label className="form-label" style={{ color: "var(--primary)", fontSize: "14px", fontWeight: "700" }}>
              🔍 型式（モデルコード）から検索（推奨）
            </label>
            <p style={{ fontSize: "12px", color: "var(--text-muted)", marginBottom: "12px" }}>
              ※車検証の「型式」欄に記載されている英数字を入力してください（例：ZVW50, JF3, E13）。
            </p>
            <div style={{ display: "flex", gap: "10px" }}>
              <input
                type="text"
                className="input-text"
                placeholder="例：ZVW50"
                value={modelSearchCode}
                onChange={(e) => setModelSearchCode(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    e.preventDefault();
                    handleModelSearch();
                  }
                }}
                style={{ flex: 1 }}
              />
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleModelSearch}
                style={{ padding: "0 24px", minWidth: "100px", height: "46px" }}
              >
                検索
              </button>
            </div>

            {searchStatus && (
              <div style={{
                marginTop: "12px",
                padding: "10px 14px",
                borderRadius: "8px",
                fontSize: "12px",
                fontWeight: "500",
                display: "flex",
                alignItems: "center",
                gap: "6px",
                background: searchStatus.success ? "var(--success-bg)" : "var(--error-bg)",
                color: searchStatus.success ? "var(--success)" : "var(--error)",
                border: `1px solid ${searchStatus.success ? "rgba(16, 185, 129, 0.15)" : "rgba(239, 68, 68, 0.15)"}`
              }}>
                <span>{searchStatus.success ? "✓" : "⚠️"}</span>
                <span>{searchStatus.message}</span>
              </div>
            )}
          </div>

          {/* Maker */}
          <div className="form-group">
            <label className="form-label">
              メーカー <span className="required-badge">必須</span>
            </label>
            <input
              type="text"
              className="input-text"
              placeholder="例：トヨタ"
              value={data.maker}
              onChange={(e) => handleChange("maker", e.target.value)}
              onBlur={() => handleBlur("maker")}
            />
            {touched.maker && errors.maker && (
              <span className="field-error-msg">{errors.maker}</span>
            )}
          </div>

          {/* Car Name */}
          <div className="form-group">
            <label className="form-label">
              車名 <span className="required-badge">必須</span>
            </label>
            <input
              type="text"
              className="input-text"
              placeholder="例：プリウス"
              value={data.carName}
              onChange={(e) => handleChange("carName", e.target.value)}
              onBlur={() => handleBlur("carName")}
            />
            {touched.carName && errors.carName && (
              <span className="field-error-msg">{errors.carName}</span>
            )}
          </div>

          {/* First Registration Year Month */}
          <div className="form-group">
            <label className="form-label">
              初度登録年月 <span className="required-badge">必須</span>
            </label>
            <input
              type="month"
              className="input-text"
              value={data.firstRegistrationYearMonth}
              onChange={(e) => handleChange("firstRegistrationYearMonth", e.target.value)}
              onBlur={() => handleBlur("firstRegistrationYearMonth")}
            />
            {touched.firstRegistrationYearMonth && errors.firstRegistrationYearMonth && (
              <span className="field-error-msg">{errors.firstRegistrationYearMonth}</span>
            )}
          </div>

          {/* Vehicle Type */}
          <div className="form-group">
            <label className="form-label">
              車両タイプ <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              {[
                { code: "KEI", name: "軽自動車" },
                { code: "COMPACT", name: "コンパクトカー" },
                { code: "SEDAN", name: "セダン" },
                { code: "MINIVAN", name: "ミニバン" },
                { code: "SUV", name: "SUV" },
              ].map((item) => (
                <div
                  key={item.code}
                  className={`radio-card ${data.vehicleType === item.code ? "active" : ""}`}
                  onClick={() => {
                    setTouched((prev) => ({ ...prev, vehicleType: true }));
                    handleChange("vehicleType", item.code);
                  }}
                >
                  <input
                    type="radio"
                    name="vehicleType"
                    checked={data.vehicleType === item.code}
                    onChange={() => {}}
                  />
                  <span>{item.name}</span>
                </div>
              ))}
            </div>
            {touched.vehicleType && !data.vehicleType && (
              <span className="field-error-msg">必須項目です</span>
            )}
          </div>

          {/* Vehicle Insurance */}
          <div className="form-group">
            <label className="form-label">
              車両保険の付帯 <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              <div
                className={`radio-card ${data.vehicleInsurance === true ? "active" : ""}`}
                onClick={() => handleChange("vehicleInsurance", true)}
              >
                <input
                  type="radio"
                  name="vehicleInsurance"
                  checked={data.vehicleInsurance === true}
                  onChange={() => {}}
                />
                <span>付帯する</span>
              </div>
              <div
                className={`radio-card ${data.vehicleInsurance === false ? "active" : ""}`}
                onClick={() => handleChange("vehicleInsurance", false)}
              >
                <input
                  type="radio"
                  name="vehicleInsurance"
                  checked={data.vehicleInsurance === false}
                  onChange={() => {}}
                />
                <span>付帯しない</span>
              </div>
            </div>
          </div>
        </div>

        {/* サイドバーのヘルプガイド */}
        <div className="help-sidebar">
          <h3>ℹ️ 車両情報と料率</h3>
          <p style={{ marginBottom: "12px" }}>
            <strong>車両タイプ</strong>: コンパクトカー（0.95倍）や軽自動車（0.90倍）は事故損害が比較的小さいため優遇されます。SUV（1.15倍）やミニバン（1.10倍）は割増が適用されます。
          </p>
          <p>
            <strong>車両保険</strong>: ご自身の車の修理費用を補償します。付帯する場合は一律で +30,000円 が加算されます。
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
