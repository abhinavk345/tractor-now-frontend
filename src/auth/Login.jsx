import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

import { useLanguage } from "../context/LanguageContext";
import LanguageSwitcher from "../components/common/LanguageSwitcher";
import { loginUser } from "../services/AuthService";

import "./Login.css";

const Login = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [role, setRole] = useState("farmer");
  const [mobile, setMobile] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSocialLogin = (provider) => {
  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

  // Preserve the selected role for backend OAuth2 processing.
  window.location.href =
    `${apiBaseUrl}/oauth2/authorization/${provider}?role=${role}`;
};


const handleLogin = async (event) => {
  event.preventDefault();

  // 1. Mobile number validation
  if (!/^[6-9]\d{9}$/.test(mobile)) {
    await Swal.fire({
      icon: "warning",
      title: "Invalid Mobile Number",
      text: "Please enter a valid 10-digit mobile number.",
      confirmButtonColor: "#2e7d32",
    });
    return;
  }

  // 2. Password validation
  if (!password.trim()) {
    await Swal.fire({
      icon: "warning",
      title: "Password Required",
      text: "Please enter your password.",
      confirmButtonColor: "#2e7d32",
    });
    return;
  }

  try {
    setLoading(true);

    // 3. Backend login payload
    const loginData = {
      login: "gh",
      mobile: mobile,
      username: "rahul.kumar@gmail.com",
      password: password,
    };

    // 4. Call Spring Boot login API
    const response = await loginUser(loginData);

    console.log("Login API Response:", response);

    // 5. Check unsuccessful backend response
    if (
      response?.success === false ||
      response?.status === "ERROR" ||
      response?.status === "FAILURE"
    ) {
      throw new Error(
        response?.message ||
        "Invalid username or password."
      );
    }

    // 6. Store authentication token
    if (response?.token) {
      localStorage.setItem("token", response.token);
    }

    if (response?.accessToken) {
      localStorage.setItem("token", response.accessToken);
    }

    // 7. Store user details
    if (response?.user) {
      localStorage.setItem(
        "user",
        JSON.stringify(response.user)
      );
    }

    // 8. Success popup
    await Swal.fire({
      icon: "success",
      title: "Login Successful!",
      text: "Welcome to TractorNow!",
      confirmButtonText: "Continue",
      confirmButtonColor: "#2e7d32",
      timer: 2000,
      timerProgressBar: true,
    });

    // 9. Navigate according to selected role
    if (role === "farmer") {
      navigate("/farmer/home");
    } else {
      navigate("/owner/dashboard");
    }

  } catch (error) {
    console.error("Login Error:", error);

    // 10. Error popup
    await Swal.fire({
      icon: "error",
      title: "Login Failed!",
      text:
        error?.message ||
        "Invalid username or password. Please try again.",
      confirmButtonText: "Try Again",
      confirmButtonColor: "#d33",
    });

  } finally {
    // 11. Reset loading state
    setLoading(false);
  }
};


  return (
    <div className="login-page">
      <LanguageSwitcher />

      <div className="login-container">

        {/* BRAND */}
        <div className="brand-section">
          <div className="brand-logo">🚜</div>

          <h1>{t("app.name")}</h1>
          <p>{t("app.tagline")}</p>
        </div>

        {/* LOGIN CARD */}
        <div className="login-card">

          <div className="login-header">
            <h2>{t("auth.login")}</h2>
            <p>{t("app.tagline")}</p>
          </div>

          {/* ROLE SELECTION */}
          <div className="role-container">

            <button
              type="button"
              className={`role-button ${
                role === "farmer" ? "active" : ""
              }`}
              onClick={() => setRole("farmer")}
            >
              <span className="role-icon">👨‍🌾</span>
              <span>{t("auth.farmer")}</span>
            </button>

            <button
              type="button"
              className={`role-button ${
                role === "owner" ? "active" : ""
              }`}
              onClick={() => setRole("owner")}
            >
              <span className="role-icon">🚜</span>
              <span>{t("auth.equipmentOwner")}</span>
            </button>

          </div>

          {/* LOGIN FORM */}
          <form onSubmit={handleLogin}>

            {/* MOBILE */}
            <div className="form-group">
              <label htmlFor="mobile">
                {t("auth.mobile")}
              </label>

              <div className="mobile-input">
                <span className="country-code">+91</span>

                <input
                  id="mobile"
                  type="tel"
                  value={mobile}
                  maxLength={10}
                  inputMode="numeric"
                  autoComplete="tel"
                  placeholder="9876543210"
                  onChange={(event) => {
                    const value = event.target.value.replace(
                      /\D/g,
                      ""
                    );
                    setMobile(value);
                  }}
                  required
                />
              </div>
            </div>

            {/* PASSWORD */}
            <div className="form-group">
              <label htmlFor="password">
                {t("auth.password")}
              </label>

              <div className="password-input">
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
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
                    setShowPassword((previous) => !previous)
                  }
                  aria-label={
                    showPassword ? "Hide password" : "Show password"
                  }
                >
                  {showPassword ? "🙈" : "👁️"}
                </button>
              </div>
            </div>

            {/* FORGOT PASSWORD */}
            <div className="forgot-password">
              <Link to="/forgot-password">
                {t("auth.forgotPassword")}
              </Link>
            </div>

            {/* LOGIN BUTTON */}
            <button
              type="submit"
              className="login-button"
              disabled={loading}
            >
              {loading ? "Signing In..." : t("auth.login")}
            </button>
            {/* SOCIAL LOGIN DIVIDER */}
<div className="social-login-divider">
  <span>{t("auth.orContinueWith")}</span>
</div>

{/* GOOGLE LOGIN */}
<button
  type="button"
  className="social-login-button google-login"
  onClick={() => handleSocialLogin("google")}
  disabled={loading}
>
  <img
    src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
    alt="Google"
  />
  <span>{t("auth.continueWithGoogle")}</span>
</button>

{/* FACEBOOK LOGIN */}
<button
  type="button"
  className="social-login-button facebook-login"
  onClick={() => handleSocialLogin("facebook")}
  disabled={loading}
>
  <span className="facebook-icon">f</span>
  <span>{t("auth.continueWithFacebook")}</span>
</button>

          </form>

          {/* REGISTER */}
          <div className="register-section">
            <p>{t("auth.dontHaveAccount")}</p>

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

        {/* FOOTER */}
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