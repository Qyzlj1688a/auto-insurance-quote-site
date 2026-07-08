import React from "react";
import { QuoteCreateRequest } from "../../types";

interface StepCoveragesProps {
  data: QuoteCreateRequest;
  updateData: (updates: Partial<QuoteCreateRequest>) => void;
  onNext: () => void;
  onPrev: () => void;
}

export const StepCoverages: React.FC<StepCoveragesProps> = ({
  data,
  updateData,
  onNext,
  onPrev,
}) => {
  const handleChange = (field: string, value: any) => {
    updateData({ [field]: value });
  };

  const isFormValid =
    !!data.propertyDamageLimit &&
    !!data.personalInjuryAmount &&
    data.lawyerOption !== null &&
    data.roadService !== null;

  return (
    <div className="wizard-card">
      <h2 className="step-title">補償条件</h2>
      <p className="step-desc">ご希望の補償内容を選択してください。</p>

      <div className="selector-layout">
        <div className="form-fields">
          {/* 対人賠償制限額 - 読み取り専用 */}
          <div className="form-group">
            <label className="form-label">
              対人賠償責任保険 <span className="required-badge" style={{ background: "#64748b" }}>固定</span>
            </label>
            <input
              type="text"
              className="input-text"
              value="無制限"
              disabled
              style={{ background: "#f1f5f9", color: "#64748b", cursor: "not-allowed" }}
            />
            <span className="field-error-msg" style={{ color: "#64748b" }}>
              対人賠償責任保険は本課題では無制限固定です
            </span>
          </div>

          {/* 対物賠償制限額 */}
          <div className="form-group">
            <label className="form-label">
              対物賠償責任保険 <span className="required-badge">必填</span>
            </label>
            <div className="radio-grid">
              {[
                { code: "UNLIMITED", name: "無制限" },
                { code: "THIRTY_MILLION", name: "3,000万円" },
              ].map((item) => (
                <div
                  key={item.code}
                  className={`radio-card ${data.propertyDamageLimit === item.code ? "active" : ""}`}
                  onClick={() => handleChange("propertyDamageLimit", item.code)}
                >
                  <input
                    type="radio"
                    name="propertyDamageLimit"
                    checked={data.propertyDamageLimit === item.code}
                    onChange={() => {}}
                  />
                  <span>{item.name}</span>
                </div>
              ))}
            </div>
          </div>

          {/* 人身傷害補償額 */}
          <div className="form-group">
            <label className="form-label">
              人身傷害保険 <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              {[
                { code: "UNLIMITED", name: "無制限" },
                { code: "FIFTY_MILLION", name: "5,000万円" },
                { code: "THIRTY_MILLION", name: "3,000万円" },
              ].map((item) => (
                <div
                  key={item.code}
                  className={`radio-card ${data.personalInjuryAmount === item.code ? "active" : ""}`}
                  onClick={() => handleChange("personalInjuryAmount", item.code)}
                >
                  <input
                    type="radio"
                    name="personalInjuryAmount"
                    checked={data.personalInjuryAmount === item.code}
                    onChange={() => {}}
                  />
                  <span>{item.name}</span>
                </div>
              ))}
            </div>
          </div>

          {/* 弁護士費用特約 */}
          <div className="form-group">
            <label className="form-label">
              弁護士費用特約 <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              <div
                className={`radio-card ${data.lawyerOption === true ? "active" : ""}`}
                onClick={() => handleChange("lawyerOption", true)}
              >
                <input
                  type="radio"
                  name="lawyerOption"
                  checked={data.lawyerOption === true}
                  onChange={() => {}}
                />
                <span>あり</span>
              </div>
              <div
                className={`radio-card ${data.lawyerOption === false ? "active" : ""}`}
                onClick={() => handleChange("lawyerOption", false)}
              >
                <input
                  type="radio"
                  name="lawyerOption"
                  checked={data.lawyerOption === false}
                  onChange={() => {}}
                />
                <span>なし</span>
              </div>
            </div>
          </div>

          {/* ロードサービス */}
          <div className="form-group">
            <label className="form-label">
              ロードサービス <span className="required-badge">必須</span>
            </label>
            <div className="radio-grid">
              <div
                className={`radio-card ${data.roadService === true ? "active" : ""}`}
                onClick={() => handleChange("roadService", true)}
              >
                <input
                  type="radio"
                  name="roadService"
                  checked={data.roadService === true}
                  onChange={() => {}}
                />
                <span>あり</span>
              </div>
              <div
                className={`radio-card ${data.roadService === false ? "active" : ""}`}
                onClick={() => handleChange("roadService", false)}
              >
                <input
                  type="radio"
                  name="roadService"
                  checked={data.roadService === false}
                  onChange={() => {}}
                />
                <span>なし</span>
              </div>
            </div>
          </div>
        </div>

        {/* サイドバーのヘルプガイド */}
        <div className="help-sidebar">
          <h3>ℹ️ 補償内容と加算額</h3>
          <p style={{ marginBottom: "12px" }}>
            <strong>対物賠償</strong>: 相手の車や建物への損害補償です。無制限を選択された場合は一律 +5,000円 が加算されます。
          </p>
          <p style={{ marginBottom: "12px" }}>
            <strong>人身傷害</strong>: お客様ご自身や同乗者のケガの補償です。5,000万円で +3,000円、無制限で +7,000円 が加算されます。
          </p>
          <p style={{ marginBottom: "12px" }}>
            <strong>弁護士特約</strong>: 事故被害時の交渉を弁護士に依頼する費用を補償します。付帯時は +2,000円 が加算されます。
          </p>
          <p>
            <strong>ロードサービス</strong>: レッカー移動やバッテリー上がり時のサービスです。付帯時は +1,500円 が加算されます。
          </p>
        </div>
      </div>

      <div className="actions-area">
        <button className="btn btn-secondary" onClick={onPrev}>
          戻る
        </button>
        <button className="btn btn-primary" onClick={onNext} disabled={!isFormValid}>
          入力内容を確認する
        </button>
      </div>
    </div>
  );
};
