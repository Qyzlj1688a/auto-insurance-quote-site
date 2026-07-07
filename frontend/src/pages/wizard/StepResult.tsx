import React from "react";
import { QuoteResultResponse } from "../../types";

interface StepResultProps {
  result: QuoteResultResponse;
  onReset: () => void;
  onModify: () => void;
  isLookupMode?: boolean;
}

export const StepResult: React.FC<StepResultProps> = ({
  result,
  onReset,
  onModify,
  isLookupMode = false,
}) => {
  return (
    <div className="wizard-card">
      <div className="result-banner">
        <span className="result-banner-icon">✓</span>
        <h2>見積が完了しました</h2>
        <p style={{ fontSize: "13px", marginTop: "4px" }}>
          入力条件に基づく概算見積結果です。
        </p>
      </div>

      <div className="form-group" style={{ textAlign: "center", marginBottom: "32px" }}>
        <span className="form-label" style={{ color: "var(--text-muted)", fontSize: "13px" }}>
          見積番号
        </span>
        <div
          style={{
            fontSize: "24px",
            fontWeight: "700",
            letterSpacing: "0.05em",
            fontFamily: "Outfit, sans-serif",
            color: "var(--primary)",
            padding: "8px 20px",
            background: "var(--primary-light)",
            borderRadius: "10px",
            display: "inline-block",
            marginTop: "4px",
          }}
        >
          {result.quoteNo}
        </div>
        <p style={{ fontSize: "11px", color: "var(--text-muted)", marginTop: "8px" }}>
          見積番号を控えておくと、後から結果を確認できます。
        </p>
      </div>

      <div className="result-premium-grid">
        <div className="premium-card">
          <div className="premium-card-title">年間保険料（概算）</div>
          <div className="premium-card-val">
            {result.annualPremium.toLocaleString()} <span style={{ fontSize: "15px" }}>円</span>
          </div>
        </div>
        <div className="premium-card">
          <div className="premium-card-title">月額保険料（概算）</div>
          <div className="premium-card-val">
            {result.monthlyPremium.toLocaleString()} <span style={{ fontSize: "15px" }}>円</span>
          </div>
        </div>
      </div>

      <div className="breakdown-section">
        <h3 className="breakdown-title">🧮 保険料の計算内訳</h3>
        <div style={{ background: "#f8fafc", borderRadius: "var(--radius-md)", padding: "8px 0" }}>
          {result.breakdowns && result.breakdowns.length > 0 ? (
            result.breakdowns.map((item, idx) => (
              <div
                key={item.itemCode}
                className="breakdown-item"
                style={{
                  animationDelay: `${idx * 0.05}s`,
                  borderBottom: idx === result.breakdowns.length - 1 ? "none" : "1px solid var(--border)",
                }}
              >
                <span style={{ fontWeight: "500" }}>{item.itemName}</span>
                <span style={{ fontWeight: "600", fontFamily: "Outfit, sans-serif" }}>
                  {item.rate !== null && item.rate !== undefined ? (
                    <span style={{ color: "var(--primary)" }}>×{Number(item.rate).toFixed(2)}</span>
                  ) : item.amount !== null && item.amount !== undefined ? (
                    <span style={{ color: "var(--success)" }}>+{Number(item.amount).toLocaleString()} 円</span>
                  ) : (
                    "-"
                  )}
                </span>
              </div>
            ))
          ) : (
            <div style={{ padding: "20px", textAlign: "center", color: "var(--text-muted)" }}>
              内訳はありません。
            </div>
          )}
        </div>
      </div>

      <div className="actions-area" style={{ justifyContent: "center", gap: "20px" }}>
        {!isLookupMode && (
          <button className="btn btn-secondary" onClick={onModify} style={{ minWidth: "160px" }}>
            条件を修正する
          </button>
        )}
        <button className="btn btn-primary" onClick={onReset} style={{ minWidth: "160px" }}>
          {isLookupMode ? "トップに戻る" : "もう一度見積する"}
        </button>
      </div>
    </div>
  );
};
