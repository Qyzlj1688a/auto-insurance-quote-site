import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { adminLogin } from "../../api/quoteApi";

export const StepAdminLogin: React.FC = () => {
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage("");

    if (!loginId.trim() || !password.trim()) {
      setErrorMessage("ログインIDとパスワードは必須項目です。");
      return;
    }

    setLoading(true);
    try {
      const response = await adminLogin({ loginId, password });
      // Save server-issued JWT token
      if (response.token) {
        sessionStorage.setItem("adminToken", response.token);
      }
      sessionStorage.setItem("adminName", response.displayName || "管理者");

      // Navigate to admin dashboard quotes listing
      navigate("/admin/quotes");
    } catch (error: any) {
      if (error.response && error.response.status === 401) {
        setErrorMessage("ログインIDまたはパスワードが正しくありません。");
      } else {
        setErrorMessage("システムエラーが発生しました。");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-login-container">
      <div className="card admin-login-card">
        <h2 className="step-title">管理者ログイン</h2>
        <p className="step-subtitle">管理画面を利用するにはログインしてください。</p>

        <form onSubmit={handleLogin} className="admin-login-form">
          <div className="form-group">
            <label className="form-label" htmlFor="loginId">
              ログインID <span className="badge-required">必須</span>
            </label>
            <input
              type="text"
              id="loginId"
              className="form-control"
              value={loginId}
              onChange={(e) => setLoginId(e.target.value)}
              placeholder="ログインIDを入力してください"
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              パスワード <span className="badge-required">必須</span>
            </label>
            <input
              type="password"
              id="password"
              className="form-control"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="パスワードを入力してください"
              disabled={loading}
            />
          </div>

          {errorMessage && (
            <div className="error-alert">
              <span className="error-icon">⚠️</span>
              <span className="error-text">{errorMessage}</span>
            </div>
          )}

          <div className="wizard-actions" style={{ justifyContent: "center", marginTop: "2rem" }}>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? "ログイン中..." : "ログイン"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
