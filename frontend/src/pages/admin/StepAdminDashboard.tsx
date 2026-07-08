import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { searchAdminQuotes, exportQuotesCsvBlob, getQuoteByQuoteNo } from "../../api/quoteApi";
import { QuoteResultResponse } from "../../types";

export const StepAdminDashboard: React.FC = () => {
  const [quoteNo, setQuoteNo] = useState("");
  const [createDateFrom, setCreateDateFrom] = useState("");
  const [createDateTo, setCreateDateTo] = useState("");
  const [quotes, setQuotes] = useState<QuoteResultResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [adminName, setAdminName] = useState("管理者");
  const [selectedQuote, setSelectedQuote] = useState<any>(null); // 詳細モーダル表示用の見積データ
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const token = sessionStorage.getItem("adminToken");
    const name = sessionStorage.getItem("adminName");
    if (!token) {
      navigate("/admin/login");
      return;
    }
    if (name) {
      setAdminName(name);
    }
    // 初回検索を実行
    handleSearch();
  }, [navigate]);

  const handleSearch = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    setErrorMessage("");
    setLoading(true);

    try {
      const data = await searchAdminQuotes(quoteNo, createDateFrom, createDateTo);
      setQuotes(data);
    } catch (error: any) {
      if (error.response && error.response.status === 401) {
        sessionStorage.clear();
        navigate("/admin/login");
      } else if (error.response && error.response.data && error.response.data.message) {
        setErrorMessage(error.response.data.message);
      } else {
        setErrorMessage("データの取得に失敗しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    sessionStorage.clear();
    navigate("/admin/login");
  };

  const handleExportCsv = async () => {
    setErrorMessage("");
    try {
      const blob = await exportQuotesCsvBlob(quoteNo, createDateFrom, createDateTo);
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `quotes_${new Date().toISOString().slice(0,10)}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (error: any) {
      if (error.response && error.response.status === 401) {
        sessionStorage.clear();
        navigate("/admin/login");
      } else {
        setErrorMessage("CSVの出力に失敗しました。");
      }
    }
  };

  const handleShowDetail = async (targetQuoteNo: string) => {
    setDetailLoading(true);
    setErrorMessage("");
    try {
      // 見積番号をキーとして計算内訳を含む詳細データを取得
      const data = await getQuoteByQuoteNo(targetQuoteNo);
      setSelectedQuote(data);
      setDetailModalOpen(true);
    } catch (error: any) {
      if (error.response && error.response.status === 401) {
        sessionStorage.clear();
        navigate("/admin/login");
      } else {
        setErrorMessage("詳細情報の取得に失敗しました。");
      }
    } finally {
      setDetailLoading(false);
    }
  };

  // テーブル表示用のマッピングヘルパー関数
  const mapLicenseColor = (code: string) => {
    switch (code) {
      case "GOLD": return "ゴールド";
      case "BLUE": return "ブルー";
      case "GREEN": return "グリーン";
      default: return code;
    }
  };

  const mapUsageType = (code: string) => {
    switch (code) {
      case "PRIVATE": return "日常・レジャー";
      case "COMMUTE": return "通勤・通学";
      case "BUSINESS": return "業務使用";
      default: return code;
    }
  };

  const getDriverRangeName = (val: string) => {
    if (val === "SELF") return "本人限定";
    if (val === "COUPLE") return "夫婦限定";
    if (val === "FAMILY") return "家族限定";
    if (val === "ANYONE") return "限定なし";
    return val;
  };

  const mapVehicleType = (code: string) => {
    switch (code) {
      case "KEI": return "軽自動車";
      case "COMPACT": return "コンパクトカー";
      case "SEDAN": return "セダン";
      case "MINIVAN": return "ミニバン";
      case "SUV": return "SUV";
      default: return code;
    }
  };

  const mapPropertyDamage = (val: string) => {
    if (val === "UNLIMITED") return "無制限";
    if (val === "THIRTY_MILLION") return "3,000万円";
    return val;
  };

  const mapPersonalInjury = (val: string) => {
    if (val === "UNLIMITED") return "無制限";
    if (val === "FIFTY_MILLION") return "5,000万円";
    if (val === "THIRTY_MILLION") return "3,000万円";
    return val;
  };

  const formatDateTime = (isoString: string) => {
    try {
      const date = new Date(isoString);
      if (isNaN(date.getTime())) return isoString;
      return date.toLocaleString("ja-JP", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
      }).replace(/\//g, "-");
    } catch (e) {
      return isoString;
    }
  };

  return (
    <div className="admin-dashboard-container">
      {/* Admin Subheader Bar */}
      <div className="admin-header-bar">
        <div className="admin-info">
          <span className="admin-user-icon">👤</span>
          ログイン中: <strong className="admin-name">{adminName}</strong>
        </div>
        <button onClick={handleLogout} className="btn btn-secondary btn-sm">
          ログアウト
        </button>
      </div>

      <div className="dashboard-content">
        <h2 className="step-title">見積一覧</h2>
        <p className="step-subtitle">条件を指定して見積情報を検索できます。</p>

        {/* Filter Card */}
        <div className="card filter-card">
          <form onSubmit={handleSearch} className="filter-form">
            <div className="filter-grid">
              <div className="form-group">
                <label className="form-label" htmlFor="filterQuoteNo">見積番号</label>
                <input
                  type="text"
                  id="filterQuoteNo"
                  className="form-control"
                  value={quoteNo}
                  onChange={(e) => setQuoteNo(e.target.value)}
                  placeholder="例：EST202606230001"
                />
              </div>
              <div className="form-group">
                <label className="form-label" htmlFor="filterDateFrom">作成日（開始）</label>
                <input
                  type="date"
                  id="filterDateFrom"
                  className="form-control"
                  value={createDateFrom}
                  onChange={(e) => setCreateDateFrom(e.target.value)}
                />
              </div>
              <div className="form-group">
                <label className="form-label" htmlFor="filterDateTo">作成日（終了）</label>
                <input
                  type="date"
                  id="filterDateTo"
                  className="form-control"
                  value={createDateTo}
                  onChange={(e) => setCreateDateTo(e.target.value)}
                />
              </div>
            </div>

            <div className="filter-actions">
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? "検索中..." : "検索"}
              </button>
              <button type="button" onClick={handleExportCsv} className="btn btn-outline" disabled={loading || quotes.length === 0}>
                CSV出力
              </button>
            </div>
          </form>
        </div>

        {errorMessage && (
          <div className="error-alert" style={{ marginBottom: "1.5rem" }}>
            <span className="error-icon">⚠️</span>
            <span className="error-text">{errorMessage}</span>
          </div>
        )}

        {/* Quotes Table List */}
        <div className="card table-card">
          {loading ? (
            <div className="table-loading-spinner">データを読み込み中...</div>
          ) : quotes.length === 0 ? (
            <div className="table-empty-state">
              <div className="empty-icon">📭</div>
              <p className="empty-text">該当する見積はありません</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="admin-table">
                <thead>
                  <tr>
                    <th>見積番号</th>
                    <th>作成日時</th>
                    <th>年間保険料</th>
                    <th>月額保険料</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  {quotes.map((q) => (
                    <tr key={q.quoteNo}>
                      <td className="quote-number-cell">{q.quoteNo}</td>
                      <td>{formatDateTime(q.createdAt)}</td>
                      <td className="premium-cell">{q.annualPremium.toLocaleString()}円</td>
                      <td className="premium-cell">{q.monthlyPremium.toLocaleString()}円</td>
                      <td>
                        <button
                          type="button"
                          onClick={() => handleShowDetail(q.quoteNo)}
                          className="btn btn-secondary btn-sm"
                          disabled={detailLoading}
                        >
                          詳細
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* 見積詳細モーダルダイアログ */}
      {detailModalOpen && selectedQuote && (
        <div className="modal-overlay" onClick={() => setDetailModalOpen(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3 className="modal-title">見積詳細 - {selectedQuote.quoteNo}</h3>
              <button className="modal-close-btn" onClick={() => setDetailModalOpen(false)}>&times;</button>
            </div>

            <div className="modal-body">
              <div className="detail-summary-grid">
                <div className="summary-item">
                  <span className="summary-label">見積番号</span>
                  <strong className="summary-value highlight">{selectedQuote.quoteNo}</strong>
                </div>
                <div className="summary-item">
                  <span className="summary-label">作成日時</span>
                  <span className="summary-value">{formatDateTime(selectedQuote.createdAt)}</span>
                </div>
                <div className="summary-item">
                  <span className="summary-label">年間保険料</span>
                  <strong className="summary-value premium-text">{selectedQuote.annualPremium.toLocaleString()} 円</strong>
                </div>
                <div className="summary-item">
                  <span className="summary-label">月額保険料</span>
                  <strong className="summary-value premium-text">{selectedQuote.monthlyPremium.toLocaleString()} 円</strong>
                </div>
              </div>

              <h4 className="detail-section-title" style={{ marginTop: "24px" }}>見積条件 (Quote Conditions)</h4>
              <div className="detail-conditions-container" style={{
                display: "grid",
                gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
                gap: "16px",
                marginBottom: "24px",
                fontSize: "13px"
              }}>
                {/* 運転者・契約情報 */}
                <div style={{ background: "#f8fafc", padding: "16px", borderRadius: "8px", border: "1px solid var(--border)" }}>
                  <div style={{ fontWeight: "700", color: "var(--primary)", marginBottom: "10px", borderBottom: "1px solid var(--border)", paddingBottom: "4px" }}>👤 運転者・契約者</div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>年齢:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.driverAge} 歳</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>免許証の色:</span>
                    <span style={{ fontWeight: "600" }}>{mapLicenseColor(selectedQuote.licenseColor)}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>使用目的:</span>
                    <span style={{ fontWeight: "600" }}>{mapUsageType(selectedQuote.usageType)}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>年間走行距離:</span>
                    <span style={{ fontWeight: "600" }}>{Number(selectedQuote.annualMileage).toLocaleString()} km</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>運転者範囲:</span>
                    <span style={{ fontWeight: "600" }}>{getDriverRangeName(selectedQuote.driverRange)}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>他社加入状況:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.hasCurrentInsurance ? "あり" : "なし"}</span>
                  </div>
                  {selectedQuote.hasCurrentInsurance && (
                    <>
                      <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                        <span style={{ color: "var(--text-muted)" }}>現在等級:</span>
                        <span style={{ fontWeight: "600" }}>{selectedQuote.grade} 等級</span>
                      </div>
                      <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                        <span style={{ color: "var(--text-muted)" }}>事故有期間:</span>
                        <span style={{ fontWeight: "600" }}>{selectedQuote.accidentTerm} 年</span>
                      </div>
                    </>
                  )}
                </div>

                {/* 車両情報 */}
                <div style={{ background: "#f8fafc", padding: "16px", borderRadius: "8px", border: "1px solid var(--border)" }}>
                  <div style={{ fontWeight: "700", color: "var(--primary)", marginBottom: "10px", borderBottom: "1px solid var(--border)", paddingBottom: "4px" }}>🚗 投保車両</div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>メーカー:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.maker}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>車名:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.carName}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>初度登録年月:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.firstRegistrationYearMonth}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>車両タイプ:</span>
                    <span style={{ fontWeight: "600" }}>{mapVehicleType(selectedQuote.vehicleType)}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>車両保険付帯:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.vehicleInsurance ? "あり" : "なし"}</span>
                  </div>
                </div>

                {/* 補償特約 */}
                <div style={{ background: "#f8fafc", padding: "16px", borderRadius: "8px", border: "1px solid var(--border)" }}>
                  <div style={{ fontWeight: "700", color: "var(--primary)", marginBottom: "10px", borderBottom: "1px solid var(--border)", paddingBottom: "4px" }}>🛡️ 補償条件・特約</div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>対人賠償:</span>
                    <span style={{ fontWeight: "600" }}>無制限</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>対物賠償:</span>
                    <span style={{ fontWeight: "600" }}>{mapPropertyDamage(selectedQuote.propertyDamageLimit)}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>人身傷害:</span>
                    <span style={{ fontWeight: "600" }}>{mapPersonalInjury(selectedQuote.personalInjuryAmount)}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>弁護士特約:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.lawyerOption ? "あり" : "なし"}</span>
                  </div>
                  <div style={{ display: "flex", justifyContent: "space-between", margin: "4px 0" }}>
                    <span style={{ color: "var(--text-muted)" }}>ロードサービス:</span>
                    <span style={{ fontWeight: "600" }}>{selectedQuote.roadService ? "あり" : "なし"}</span>
                  </div>
                </div>
              </div>

              <h4 className="detail-section-title">計算内訳 (Premium Breakdown)</h4>
              <div className="table-responsive">
                <table className="admin-table breakdown-table">
                  <thead>
                    <tr>
                      <th>項目コード</th>
                      <th>項目名</th>
                      <th>係数 (Rate)</th>
                      <th>加算額 (Amount)</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedQuote.breakdowns
                      .sort((a: any, b: any) => a.displayOrder - b.displayOrder)
                      .map((b: any) => (
                        <tr key={b.itemCode}>
                          <td><code>{b.itemCode}</code></td>
                          <td>{b.itemName}</td>
                          <td>{b.rate !== null && b.rate !== undefined ? `${b.rate.toFixed(2)}` : "-"}</td>
                          <td>{b.amount !== null && b.amount !== undefined ? `+${b.amount.toLocaleString()}円` : "-"}</td>
                        </tr>
                      ))}
                  </tbody>
                </table>
              </div>
            </div>

            <div className="modal-footer">
              <button type="button" onClick={() => setDetailModalOpen(false)} className="btn btn-secondary">
                閉じる
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
