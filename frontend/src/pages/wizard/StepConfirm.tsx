import React, { useState } from "react";
import { QuoteCreateRequest, QuoteResultResponse } from "../../types";
import { createQuote } from "../../api/quoteApi";
import axios from "axios";

interface StepConfirmProps {
  data: QuoteCreateRequest;
  onSuccess: (response: QuoteResultResponse) => void;
  onPrev: () => void;
  goToStep: (stepIndex: number) => void;
}

export const StepConfirm: React.FC<StepConfirmProps> = ({
  data,
  onSuccess,
  onPrev,
  goToStep,
}) => {
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const getLicenseColorName = (val: string) => {
    if (val === "GOLD") return "ゴールド";
    if (val === "BLUE") return "ブルー";
    if (val === "GREEN") return "グリーン";
    return val;
  };

  const getUsageTypeName = (val: string) => {
    if (val === "PRIVATE") return "日常・レジャー";
    if (val === "COMMUTE") return "通勤・通学";
    if (val === "BUSINESS") return "業務使用";
    return val;
  };

  const getDriverRangeName = (val: string) => {
    if (val === "SELF") return "本人限定";
    if (val === "COUPLE") return "夫婦限定";
    if (val === "FAMILY") return "家族限定";
    if (val === "ANYONE") return "限定なし";
    return val;
  };

  const getVehicleTypeName = (val: string) => {
    if (val === "KEI") return "軽自動車";
    if (val === "COMPACT") return "コンパクトカー";
    if (val === "SEDAN") return "セダン";
    if (val === "MINIVAN") return "ミニバン";
    if (val === "SUV") return "SUV";
    return val;
  };

  const getPropertyDamageName = (val: string) => {
    if (val === "UNLIMITED") return "無制限";
    if (val === "THIRTY_MILLION") return "3,000万円";
    return val;
  };

  const getPersonalInjuryName = (val: string) => {
    if (val === "UNLIMITED") return "無制限";
    if (val === "FIFTY_MILLION") return "5,000万円";
    if (val === "THIRTY_MILLION") return "3,000万円";
    return val;
  };

  const handleSubmit = async () => {
    setLoading(true);
    setErrorMessage(null);

    // 送信データの調整（他社加入状況に応じた不要パラメータのクリア）
    const payload: QuoteCreateRequest = {
      ...data,
      grade: data.hasCurrentInsurance ? data.grade : "",
      accidentTerm: data.hasCurrentInsurance ? data.accidentTerm : "",
    };

    try {
      const response = await createQuote(payload);
      onSuccess(response);
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        const errorData = error.response.data;
        if (errorData && errorData.message) {
          setErrorMessage(errorData.message);
        } else {
          setErrorMessage("入力内容に誤りがあります。");
        }
      } else {
        setErrorMessage("システムエラーが発生しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="wizard-card">
      <h2 className="step-title">入力確認</h2>
      <p className="step-desc">入力内容と概算保険料をご確認ください。</p>

      {errorMessage && (
        <div className="validation-bar">
          <span>⚠️ {errorMessage}</span>
        </div>
      )}

      <div className="summary-container">
        {/* 左側カラム：入力内容の要約 */}
        <div className="summary-blocks">
          {/* 使用者情報 */}
          <div className="summary-section">
            <div className="summary-section-title">
              <span>使用者情報</span>
              <button className="btn btn-secondary" style={{ padding: "4px 10px", fontSize: "12px" }} onClick={() => goToStep(1)}>
                変更
              </button>
            </div>
            <div className="summary-row">
              <span className="summary-label">運転者年齢</span>
              <span className="summary-val">{data.driverAge} 歳</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">免許証の色</span>
              <span className="summary-val">{getLicenseColorName(data.licenseColor)}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">使用目的</span>
              <span className="summary-val">{getUsageTypeName(data.usageType)}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">年間走行距離</span>
              <span className="summary-val">{Number(data.annualMileage).toLocaleString()} km</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">運転者範囲</span>
              <span className="summary-val">{getDriverRangeName(data.driverRange)}</span>
            </div>
          </div>

          {/* 契約中保険 */}
          <div className="summary-section">
            <div className="summary-section-title">
              <span>契約中保険</span>
              <button className="btn btn-secondary" style={{ padding: "4px 10px", fontSize: "12px" }} onClick={() => goToStep(2)}>
                変更
              </button>
            </div>
            <div className="summary-row">
              <span className="summary-label">現在加入有無</span>
              <span className="summary-val">{data.hasCurrentInsurance ? "加入している" : "加入していない"}</span>
            </div>
            {data.hasCurrentInsurance && (
              <>
                <div className="summary-row">
                  <span className="summary-label">等級</span>
                  <span className="summary-val">{data.grade} 等級</span>
                </div>
                <div className="summary-row">
                  <span className="summary-label">事故有係数適用期間</span>
                  <span className="summary-val">{data.accidentTerm} 年</span>
                </div>
              </>
            )}
          </div>

          {/* 車両情報 */}
          <div className="summary-section">
            <div className="summary-section-title">
              <span>車両情報</span>
              <button className="btn btn-secondary" style={{ padding: "4px 10px", fontSize: "12px" }} onClick={() => goToStep(3)}>
                変更
              </button>
            </div>
            <div className="summary-row">
              <span className="summary-label">メーカー</span>
              <span className="summary-val">{data.maker}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">車名</span>
              <span className="summary-val">{data.carName}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">初度登録年月</span>
              <span className="summary-val">{data.firstRegistrationYearMonth}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">車両タイプ</span>
              <span className="summary-val">{getVehicleTypeName(data.vehicleType)}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">車両保険の付帯</span>
              <span className="summary-val">{data.vehicleInsurance ? "付帯する" : "付帯しない"}</span>
            </div>
          </div>

          {/* 補償条件 */}
          <div className="summary-section" style={{ borderBottom: "none" }}>
            <div className="summary-section-title">
              <span>補償条件</span>
              <button className="btn btn-secondary" style={{ padding: "4px 10px", fontSize: "12px" }} onClick={() => goToStep(4)}>
                変更
              </button>
            </div>
            <div className="summary-row">
              <span className="summary-label">対人賠償責任保険</span>
              <span className="summary-val">無制限 (固定)</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">対物賠償責任保険</span>
              <span className="summary-val">{getPropertyDamageName(data.propertyDamageLimit)}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">人身傷害保険</span>
              <span className="summary-val">{getPersonalInjuryName(data.personalInjuryAmount)}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">弁護士費用特約</span>
              <span className="summary-val">{data.lawyerOption ? "あり" : "なし"}</span>
            </div>
            <div className="summary-row">
              <span className="summary-label">ロードサービス</span>
              <span className="summary-val">{data.roadService ? "あり" : "なし"}</span>
            </div>
          </div>
        </div>

        {/* 右側カラム：お見積り実行サイドバー */}
        <div className="price-sidebar">
          <div className="price-sidebar-title">概算保険料</div>
          <div className="price-row" style={{ display: "flex", alignItems: "baseline" }}>
            <span className="price-amount">??,???</span>
            <span className="price-unit"> 円</span>
          </div>
          <div className="price-note">
            ※年間保険料および月額保険料の詳細は、見積作成ボタンをクリックした後に算出されます。
          </div>
          <button
            className="btn btn-primary"
            onClick={handleSubmit}
            disabled={loading}
            style={{ width: "100%", marginTop: "24px" }}
          >
            {loading ? "作成中..." : "見積を作成する"}
          </button>
        </div>
      </div>

      <div className="actions-area">
        <button className="btn btn-secondary" onClick={onPrev} disabled={loading}>
          戻る
        </button>
      </div>
    </div>
  );
};
