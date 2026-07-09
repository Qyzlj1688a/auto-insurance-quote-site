import React, { useState, useEffect } from "react";
import { QuoteCreateRequest } from "../../types";

interface StepInsuranceProps {
  data: QuoteCreateRequest;
  updateData: (updates: Partial<QuoteCreateRequest>) => void;
  onNext: () => void;
  onPrev: () => void;
}

export const StepInsurance: React.FC<StepInsuranceProps> = ({
  data,
  updateData,
  onNext,
  onPrev,
}) => {
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [touched, setTouched] = useState<Record<string, boolean>>({});

  // 他社加入なしを選択した場合、等級と事故有期間をリセット
  const handleInsuranceChange = (hasInsurance: boolean) => {
    setTouched({});
    setErrors({});
    if (!hasInsurance) {
      updateData({
        hasCurrentInsurance: false,
        grade: "",
        accidentTerm: "",
      });
    } else {
      updateData({
        hasCurrentInsurance: true,
      });
    }
  };

  const validateField = (name: string, value: any) => {
    let errorMsg = "";
    if (data.hasCurrentInsurance === true) {
      if (name === "grade") {
        if (value === "") {
          errorMsg = "必須項目です";
        } else {
          const num = Number(value);
          if (isNaN(num) || num < 1 || num > 20) {
            errorMsg = "1〜20の等級を入力してください";
          }
        }
      } else if (name === "accidentTerm") {
        if (value === "") {
          errorMsg = "必須項目です";
        } else {
          const num = Number(value);
          if (isNaN(num) || num < 0 || num > 6) {
            errorMsg = "0〜6年の年数を入力してください";
          }
        }
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
    data.hasCurrentInsurance !== null &&
    (data.hasCurrentInsurance === false ||
      (data.grade !== "" &&
        !validateField("grade", data.grade) &&
        data.accidentTerm !== "" &&
        !validateField("accidentTerm", data.accidentTerm)));

  const handleNextClick = () => {
    if (isFormValid) {
      onNext();
    }
  };

  return (
    <div className="wizard-card">
      <h2 className="step-title">契約中保険</h2>
      <p className="step-desc">現在の他社加入状況を入力してください。</p>

      <div className="selector-layout">
        <div className="form-fields">
          {/* Has Current Insurance */}
          <div className="form-group">
            <label className="form-label">
              現在、他社の自動車保険に加入していますか <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              <div
                className={`radio-card ${data.hasCurrentInsurance === true ? "active" : ""}`}
                onClick={() => handleInsuranceChange(true)}
              >
                <input
                  type="radio"
                  name="hasCurrentInsurance"
                  checked={data.hasCurrentInsurance === true}
                  onChange={() => {}}
                />
                <span>加入している</span>
              </div>
              <div
                className={`radio-card ${data.hasCurrentInsurance === false ? "active" : ""}`}
                onClick={() => handleInsuranceChange(false)}
              >
                <input
                  type="radio"
                  name="hasCurrentInsurance"
                  checked={data.hasCurrentInsurance === false}
                  onChange={() => {}}
                />
                <span>加入していない</span>
              </div>
            </div>
          </div>

          {/* 他社加入ありの場合のみ表示する条件付き入力項目 */}
          {data.hasCurrentInsurance === true && (
            <div className="conditional-fields" style={{ animation: "slideIn 0.3s ease-out" }}>
              {/* Grade */}
              <div className="form-group">
                <label className="form-label">
                  等級 (1〜20) <span className="required-badge">必須</span>
                </label>
                <input
                  type="number"
                  className="input-text"
                  placeholder="例：20"
                  value={data.grade}
                  onChange={(e) => handleChange("grade", e.target.value === "" ? "" : Number(e.target.value))}
                  onBlur={() => handleBlur("grade")}
                />
                {touched.grade && errors.grade && (
                  <span className="field-error-msg">{errors.grade}</span>
                )}
              </div>

              {/* Accident Coefficient Period */}
              <div className="form-group">
                <label className="form-label">
                  事故有係数適用期間 (0〜6年) <span className="required-badge">必須</span>
                </label>
                <input
                  type="number"
                  className="input-text"
                  placeholder="例：0"
                  value={data.accidentTerm}
                  onChange={(e) => handleChange("accidentTerm", e.target.value === "" ? "" : Number(e.target.value))}
                  onBlur={() => handleBlur("accidentTerm")}
                />
                {touched.accidentTerm && errors.accidentTerm && (
                  <span className="field-error-msg">{errors.accidentTerm}</span>
                )}
              </div>
            </div>
          )}
        </div>

        {/* サイドバーのヘルプガイド */}
        <div className="help-sidebar">
          <h3>ℹ️ 等級制度について</h3>
          <p style={{ marginBottom: "12px" }}>
            <strong>ノンフリート等級</strong>: 新規加入時は通常「6等級」から始まり、1年間無事故であれば翌年「1等級」上がり、事故があると「3等級」下がります。等級が高いほど割引率が高くなります（20等級が最大割引の0.80）。
          </p>
          <p>
            <strong>事故有係数適用期間</strong>: 事故があった場合、同じ等級でも「事故有の割引率（高めの保険料）」が適用される期間（0〜6年）です。
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
