import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { useLanguage } from "../context/LanguageContext";
import LanguageSwitcher from "../components/common/LanguageSwitcher";

import "./Login.css";

const Login = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [role, setRole] = useState("farmer");
  const [mobile, setMobile] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = (event) => {
    event.preventDefault();

    // Temporary navigation.
    // Later this will call Spring Boot login API.

    if (role === "farmer") {
      navigate("/farmer/home");
    } else {
      navigate("/owner/dashboard");
    }
  };

  return (
    <div className="login-page">
         <LanguageSwitcher />
      <div className="login-container">
       
        {/* =========================
            BRAND
        ========================= */}

        <div className="brand-section">

          <div className="brand-logo">
            🚜
          </div>

          <h1>
            {t("app.name")}
          </h1>

          <p>
            {t("app.tagline")}
          </p>

        </div>

        {/* =========================
            LOGIN CARD
        ========================= */}

        <div className="login-card">

         
          {/* Header */}

          <div className="login-header">

            <h2>
              {t("auth.login")}
            </h2>

            <p>
              {t("app.tagline")}
            </p>

          </div>

          {/* =========================
              ROLE SELECTION
          ========================= */}

          <div className="role-container">

            <button
              type="button"
              className={`role-button ${
                role === "farmer" ? "active" : ""
              }`}
              onClick={() => setRole("farmer")}
            >
              <span className="role-icon">
                👨‍🌾
              </span>

              <span>
                {t("auth.farmer")}
              </span>
            </button>

            <button
              type="button"
              className={`role-button ${
                role === "owner" ? "active" : ""
              }`}
              onClick={() => setRole("owner")}
            >
              <span className="role-icon">
                🚜
              </span>

              <span>
                {t("auth.equipmentOwner")}
              </span>
            </button>

          </div>

          {/* =========================
              LOGIN FORM
          ========================= */}

          <form onSubmit={handleLogin}>

            {/* Mobile */}

            <div className="form-group">

              <label htmlFor="mobile">
                {t("auth.mobile")}
              </label>

              <div className="mobile-input">

                <span className="country-code">
                  +91
                </span>

                <input
                  id="mobile"
                  type="tel"
                  value={mobile}
                  maxLength={10}
                  inputMode="numeric"
                  autoComplete="tel"
                  placeholder="9876543210"
                  onChange={(event) => {
                    const value =
                      event.target.value.replace(/\D/g, "");

                    setMobile(value);
                  }}
                  required
                />

              </div>

            </div>

            {/* Password */}

            <div className="form-group">

              <label htmlFor="password">
                {t("auth.password")}
              </label>

              <div className="password-input">

                <input
                  id="password"
                  type={
                    showPassword
                      ? "text"
                      : "password"
                  }
                  value={password}
                  placeholder="••••••••"
                  autoComplete="current-password"
                  onChange={(event) =>
                    setPassword(event.target.value)
                  }
                  required
                />

                <button
                  type="button"
                  className="password-toggle"
                  onClick={() =>
                    setShowPassword(
                      (previous) => !previous
                    )
                  }
                  aria-label={
                    showPassword
                      ? "Hide password"
                      : "Show password"
                  }
                >
                  {showPassword ? "🙈" : "👁️"}
                </button>

              </div>

            </div>

            {/* Forgot Password */}

            <div className="forgot-password">

              <Link to="/forgot-password">
                {t("auth.forgotPassword")}
              </Link>

            </div>

            {/* =========================
                SIGN IN BUTTON
            ========================= */}

            <button
              type="submit"
              className="login-button"
            >
              {t("auth.login")}
            </button>

          </form>

          {/* =========================
              REGISTER
          ========================= */}

          <div className="register-section">

            <p>
              {t("auth.dontHaveAccount")}
            </p>

            <div className="register-links">

              <Link to="/register/farmer">
                {t("auth.registerAsFarmer")}
              </Link>

              <span>|</span>

              <Link to="/register/owner">
                {t("auth.registerAsOwner")}
              </Link>

            </div>

          </div>

        </div>

        {/* Footer */}

        <div className="footer">
          © {new Date().getFullYear()} {t("app.name")}
          <span>•</span>
          {t("app.tagline")}
        </div>

      </div>

    </div>
  );
};

export default Login;