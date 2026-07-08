import { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { QuoteCreateRequest, QuoteResultResponse } from "./types";
import { StepTop } from "./pages/wizard/StepTop";
import { StepUserInfo } from "./pages/wizard/StepUserInfo";
import { StepInsurance } from "./pages/wizard/StepInsurance";
import { StepVehicle } from "./pages/wizard/StepVehicle";
import { StepCoverages } from "./pages/wizard/StepCoverages";
import { StepConfirm } from "./pages/wizard/StepConfirm";
import { StepResult } from "./pages/wizard/StepResult";
import { StepAdminLogin } from "./pages/admin/StepAdminLogin";
import { StepAdminDashboard } from "./pages/admin/StepAdminDashboard";

const initialFormState: QuoteCreateRequest = {
  driverAge: "",
  licenseColor: "",
  usageType: "",
  annualMileage: "",
  driverRange: "",
  hasCurrentInsurance: null,
  grade: "",
  accidentTerm: "",
  maker: "",
  carName: "",
  firstRegistrationYearMonth: "",
  vehicleType: "",
  vehicleInsurance: null,
  propertyDamageLimit: "",
  personalInjuryAmount: "",
  lawyerOption: null,
  roadService: null,
};

function PublicWizard() {
  const [currentStep, setCurrentStep] = useState<number>(0);
  const [formData, setFormData] = useState<QuoteCreateRequest>(initialFormState);
  const [quoteResult, setQuoteResult] = useState<QuoteResultResponse | null>(null);
  const [isLookupMode, setIsLookupMode] = useState<boolean>(false);

  const updateFormData = (updates: Partial<QuoteCreateRequest>) => {
    setFormData((prev) => ({ ...prev, ...updates }));
  };

  const handleNext = () => {
    setCurrentStep((prev) => prev + 1);
  };

  const handlePrev = () => {
    setCurrentStep((prev) => prev - 1);
  };

  const handleSuccess = (result: QuoteResultResponse, fromLookup: boolean = false) => {
    setQuoteResult(result);
    setIsLookupMode(fromLookup);
    setCurrentStep(6); // 見積結果表示画面はステップ6
  };

  const handleReset = () => {
    setFormData(initialFormState);
    setQuoteResult(null);
    setIsLookupMode(false);
    setCurrentStep(0);
  };

  const handleModify = () => {
    setCurrentStep(5); // 入力確認画面（ステップ5）へ戻る
  };

  // ステップインジケータ（ステップ1〜5）を表示するかどうかの判定
  const showStepper = currentStep >= 1 && currentStep <= 5;

  const stepsList = [
    { num: 1, label: "運転者" },
    { num: 2, label: "契約状況" },
    { num: 3, label: "車両情報" },
    { num: 4, label: "補償条件" },
    { num: 5, label: "入力確認" },
  ];

  return (
    <>
      {/* ステップインジケータ（プログレスバー） */}
      {showStepper && (
        <div className="stepper">
          {stepsList.map((step) => {
            let statusClass = "";
            if (currentStep === step.num) {
              statusClass = "active";
            } else if (currentStep > step.num) {
              statusClass = "completed";
            }
            return (
              <div key={step.num} className={`step-node ${statusClass}`}>
                <div className="step-circle">{step.num}</div>
                <span className="step-label">{step.label}</span>
              </div>
            );
          })}
        </div>
      )}

      {/* お見積りステップ切り替え */}
      {currentStep === 0 && <StepTop onNext={handleNext} onLookupSuccess={(res) => handleSuccess(res, true)} />}
      {currentStep === 1 && (
        <StepUserInfo
          data={formData}
          updateData={updateFormData}
          onNext={handleNext}
          onPrev={handlePrev}
        />
      )}
      {currentStep === 2 && (
        <StepInsurance
          data={formData}
          updateData={updateFormData}
          onNext={handleNext}
          onPrev={handlePrev}
        />
      )}
      {currentStep === 3 && (
        <StepVehicle
          data={formData}
          updateData={updateFormData}
          onNext={handleNext}
          onPrev={handlePrev}
        />
      )}
      {currentStep === 4 && (
        <StepCoverages
          data={formData}
          updateData={updateFormData}
          onNext={handleNext}
          onPrev={handlePrev}
        />
      )}
      {currentStep === 5 && (
        <StepConfirm
          data={formData}
          onSuccess={(res) => handleSuccess(res, false)}
          onPrev={handlePrev}
          goToStep={(idx) => setCurrentStep(idx)}
        />
      )}
      {currentStep === 6 && quoteResult && (
        <StepResult
          result={quoteResult}
          onReset={handleReset}
          onModify={handleModify}
          isLookupMode={isLookupMode}
        />
      )}
    </>
  );
}

// ログイン保護用ルーティングラッパーコンポーネント
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const token = sessionStorage.getItem("adminToken");
  if (!token) {
    return <Navigate to="/admin/login" replace />;
  }
  return <>{children}</>;
}

function App() {
  return (
    <BrowserRouter>
      {/* ヘッダー */}
      <header className="app-header">
        <div className="header-content">
          <div className="header-logo">安特自動車保険</div>
          <div className="header-badge">自動車保険簡易見積</div>
        </div>
      </header>

      {/* メインコンテンツエリア */}
      <main className="main-content">
        <Routes>
          <Route path="/" element={<PublicWizard />} />
          <Route path="/admin/login" element={<StepAdminLogin />} />
          <Route
            path="/admin/quotes"
            element={
              <ProtectedRoute>
                <StepAdminDashboard />
              </ProtectedRoute>
            }
          />
          {/* それ以外のパスはトップにリダイレクト */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>

      {/* フッター */}
      <footer className="app-footer">
        <p>&copy; 2026 株式会社ティーアンドエス. All Rights Reserved. (簡易見積課題用デモ)</p>
      </footer>
    </BrowserRouter>
  );
}

export default App;
