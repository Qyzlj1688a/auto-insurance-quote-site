import React, { useState } from "react";
import { QuoteResultResponse } from "../../types";
import { getQuoteByQuoteNo } from "../../api/quoteApi";
import axios from "axios";

interface StepTopProps {
  onNext: () => void;
  onLookupSuccess: (result: QuoteResultResponse) => void;
}

export const StepTop: React.FC<StepTopProps> = ({ onNext, onLookupSuccess }) => {
  const [quoteNoSearch, setQuoteNoSearch] = useState("");
  const [searchError, setSearchError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleLookup = async () => {
    setSearchError(null);
    if (!quoteNoSearch.trim()) {
      setSearchError("見積番号を入力してください。");
      return;
    }
    setLoading(true);
    try {
      const response = await getQuoteByQuoteNo(quoteNoSearch);
      onLookupSuccess(response);
    } catch (error) {
      if (axios.isAxiosError(error) && error.response && error.response.status === 404) {
        setSearchError("指定された見積番号は存在しません。");
      } else {
        setSearchError("システムエラーが発生しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="wizard-card">
      <h2 className="step-title">自動車保険 簡易見積</h2>
      <p className="step-desc">
        いくつかの質問にお答えいただくだけで、年間および月額保険料の概算をスピーディーに算出します。
      </p>

      <div className="prep-guide-title">見積前にご用意いただくとスムーズです：</div>
      <div className="prep-grid">
        <div className="prep-card">
          <span className="prep-icon">🚗</span>
          車検証
        </div>
        <div className="prep-card">
          <span className="prep-icon">📄</span>
          現在の保険証券
        </div>
        <div className="prep-card">
          <span className="prep-icon">⏱️</span>
          走行距離のメモ
        </div>
        <div className="prep-card">
          <span className="prep-icon">💡</span>
          ご希望の補償内容
        </div>
      </div>

      <div className="actions-area" style={{ justifyContent: "center" }}>
        <button className="btn btn-primary" onClick={onNext} style={{ minWidth: "240px", fontSize: "16px" }}>
          見積を始める
        </button>
      </div>

      {/* Quote Lookup Area */}
      <div style={{
        marginTop: "40px",
        paddingTop: "32px",
        borderTop: "1px dashed var(--border)",
        width: "100%"
      }}>
        <label className="form-label" style={{ color: "var(--primary)", fontSize: "14px", fontWeight: "700" }}>
          💾 保存した見積を呼び出す
        </label>
        <p style={{ fontSize: "12px", color: "var(--text-muted)", marginBottom: "12px" }}>
          ※見積番号を入力してください（例：EST202606230001）。前回保存された結果を再表示します。
        </p>
        <div style={{ display: "flex", gap: "10px" }}>
          <input
            type="text"
            className="input-text"
            placeholder="見積番号を入力"
            value={quoteNoSearch}
            onChange={(e) => setQuoteNoSearch(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.preventDefault();
                handleLookup();
              }
            }}
            style={{ flex: 1 }}
          />
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleLookup}
            disabled={loading}
            style={{ padding: "0 24px", minWidth: "80px", height: "46px" }}
          >
            {loading ? "読込中" : "表示"}
          </button>
        </div>
        {searchError && (
          <span className="field-error-msg" style={{ marginTop: "8px" }}>
            {searchError}
          </span>
        )}
      </div>
    </div>
  );
};
