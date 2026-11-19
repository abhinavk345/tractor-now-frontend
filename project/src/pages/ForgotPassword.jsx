
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

import { useLanguage } from "../context/LanguageContext";
import LanguageSwitcher from "../components/common/LanguageSwitcher";

import {
  sendPasswordResetOtp,
  verifyPasswordResetOtp,
  resetPassword,
} from "../services/AuthService";

import "./ForgotPassword.css";

const ForgotPassword = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();

  // Recovery state
  const [step, setStep] = useState("request");
  const [method, setMethod] = useState("MOBILE");
  const [identifier, setIdentifier] = useState("");

  // OTP state
  const [otp, setOtp] = useState("");
  const [resendIn, setResendIn] = useState(0);

  // Password state
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  // Loading state
  const [loading, setLoading] = useState(false);

  // OTP resend countdown
  useEffect(() => {
    if (resendIn <= 0) return;

    const timer = setTimeout(() => {
      setResendIn((previous) => previous - 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [resendIn]);

  // Common API error message
  const getErrorMessage = (error, fallbackMessage) => {
    return (
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      fallbackMessage
    );
  };

  // ======================================
  // VALIDATE MOBILE / EMAIL
  // ======================================

  const validateIdentifier = () => {
    const value = identifier.trim();

    if (method === "MOBILE") {
      if (!/^[6-9]\d{9}$/.test(value)) {
        Swal.fire({
          icon: "warning",
          title: "Invalid Mobile Number",
          text: "Please enter a valid 10-digit mobile number.",
          confirmButtonColor: "#2e7d32",
        });

        return false;
      }
    }

    if (method === "EMAIL") {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

      if (!emailRegex.test(value)) {
        Swal.fire({
          icon: "warning",
          title: "Invalid Email",
          text: "Please enter a valid email address.",
          confirmButtonColor: "#2e7d32",
        });

        return false;
      }
    }

    return true;
  };

  // ======================================
  // SEND OTP
  // ======================================

  const handleSendOtp = async (event) => {
    event.preventDefault();

    if (!validateIdentifier()) return;

    try {
      setLoading(true);

      const response = await sendPasswordResetOtp({
        method,
        identifier: identifier.trim(),
      });

      if (response?.success === false) {
        throw new Error(
          response.message || "Unable to send OTP."
        );
      }

      // Continue even if account does not exist.
      // Backend intentionally returns a generic response
      // to prevent account enumeration.

      setStep("verify");
      setOtp("");
      setResendIn(30);

      await Swal.fire({
        icon: "success",
        title: "OTP Request Submitted",
        text:
          response?.message ||
          "If an account exists, an OTP has been sent to your registered contact.",
        confirmButtonColor: "#2e7d32",
      });
    } catch (error) {
      await Swal.fire({
        icon: "error",
        title: "OTP Failed",
        text: getErrorMessage(error, "Unable to send OTP."),
        confirmButtonColor: "#d33",
      });
    } finally {
      setLoading(false);
    }
  };

  // ======================================
  // RESEND OTP
  // ======================================

  const handleResendOtp = async () => {
    if (resendIn > 0 || loading) return;

    try {
      setLoading(true);

      const response = await sendPasswordResetOtp({
        method,
        identifier: identifier.trim(),
      });

      if (response?.success === false) {
        throw new Error(
          response.message || "Unable to resend OTP."
        );
      }

      setResendIn(30);
      setOtp("");

      await Swal.fire({
        icon: "success",
        title: "OTP Request Submitted",
        text:
          response?.message ||
          "If an account exists, a new OTP has been sent to your registered contact.",
        confirmButtonColor: "#2e7d32",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Resend Failed",
        text: getErrorMessage(error, "Unable to resend OTP."),
        confirmButtonColor: "#d33",
      });
    } finally {
      setLoading(false);
    }
  };

  // ======================================
  // VERIFY OTP
  // ======================================

  const handleVerifyOtp = async (event) => {
    event.preventDefault();

    if (!/^\d{6}$/.test(otp)) {
      Swal.fire({
        icon: "warning",
        title: "Invalid OTP",
        text: "Please enter a valid 6-digit OTP.",
        confirmButtonColor: "#2e7d32",
      });

      return;
    }

    try {
      setLoading(true);

      const response = await verifyPasswordResetOtp({
        method,
        identifier: identifier.trim(),
        otp,
      });

      if (response?.success === false) {
        throw new Error(
          response.message || "Invalid or expired OTP."
        );
      }

      // Backend stores the reset token in an HttpOnly cookie.
      // Do not read or store the reset token in React.

      setStep("reset");

      await Swal.fire({
        icon: "success",
        title: "OTP Verified!",
        text: "You can now create your new password.",
        confirmButtonColor: "#2e7d32",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Verification Failed",
        text: getErrorMessage(error, "Invalid or expired OTP."),
        confirmButtonColor: "#d33",
      });
    } finally {
      setLoading(false);
    }
  };

  // ======================================
  // RESET PASSWORD
  // ======================================

  const handleResetPassword = async (event) => {
    event.preventDefault();

    // Password strength validation
    const passwordRegex =
      /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

    if (!passwordRegex.test(newPassword)) {
      Swal.fire({
        icon: "warning",
        title: "Weak Password",
        text:
          "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one number and one special character.",
        confirmButtonColor: "#2e7d32",
      });

      return;
    }

    // Confirm password validation
    if (newPassword !== confirmPassword) {
      Swal.fire({
        icon: "warning",
        title: "Password Mismatch",
        text: "New password and confirm password do not match.",
        confirmButtonColor: "#2e7d32",
      });

      return;
    }

    try {
      setLoading(true);

      // Reset token is sent automatically through
      // the HttpOnly cookie set by the backend.
      // Never send resetToken from frontend.

      const response = await resetPassword({
        newPassword,
        confirmPassword,
      });

      if (response?.success === false) {
        throw new Error(
          response.message || "Password reset failed."
        );
      }

      await Swal.fire({
        icon: "success",
        title: "Password Updated!",
        text:
          "Your password has been reset successfully. Please login with your new password.",
        confirmButtonText: "Go to Login",
        confirmButtonColor: "#2e7d32",
      });

      navigate("/login");
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Reset Failed",
        text: getErrorMessage(error, "Unable to reset password."),
        confirmButtonColor: "#d33",
      });
    } finally {
      setLoading(false);
    }
  };

  // ======================================
  // CHANGE RECOVERY METHOD
  // ======================================

  const handleChangeMethod = () => {
    setStep("request");
    setOtp("");
    setResendIn(0);
    setNewPassword("");
    setConfirmPassword("");
    setShowPassword(false);
  };

  // ======================================
  // UI
  // ======================================

  return (
    <div className="forgot-page">

      <LanguageSwitcher />

      <div className="forgot-container">

        {/* BRAND */}
        <div className="forgot-brand">
          <div className="forgot-logo">🚜</div>

          <h1>{t("app.name")}</h1>

          <p>{t("app.tagline")}</p>
        </div>

        {/* FORGOT PASSWORD CARD */}
        <div className="forgot-card">

          <div className="forgot-header">

            <div className="forgot-icon">
              <span>🔐</span>
            </div>

            <h2>{t("auth.forgotPasswordTitle")}</h2>

            <p>{t("auth.recoveryInstructions")}</p>

          </div>

          {/* STEP INDICATOR */}
          <div className="forgot-steps">

            <div
              className={`forgot-step ${
                step === "request" ? "active" : "completed"
              }`}
            >
              <span>1</span>
              <small>Recovery</small>
            </div>

            <div className="step-line"></div>

            <div
              className={`forgot-step ${
                step === "verify"
                  ? "active"
                  : step === "reset"
                  ? "completed"
                  : ""
              }`}
            >
              <span>2</span>
              <small>Verify OTP</small>
            </div>

            <div className="step-line"></div>

            <div
              className={`forgot-step ${
                step === "reset" ? "active" : ""
              }`}
            >
              <span>3</span>
              <small>New Password</small>
            </div>

          </div>

          {/* ==================================
              STEP 1: SEND OTP
          ================================== */}

          {step === "request" && (

            <form onSubmit={handleSendOtp}>

              <label className="forgot-label">
                {t("auth.recoveryMethod")}
              </label>

              <div className="recovery-method">

                <button
                  type="button"
                  className={`method-button ${
                    method === "MOBILE" ? "selected" : ""
                  }`}
                  onClick={() => {
                    setMethod("MOBILE");
                    setIdentifier("");
                  }}
                  disabled={loading}
                >
                  📱 Mobile OTP
                </button>

                <button
                  type="button"
                  className={`method-button ${
                    method === "EMAIL" ? "selected" : ""
                  }`}
                  onClick={() => {
                    setMethod("EMAIL");
                    setIdentifier("");
                  }}
                  disabled={loading}
                >
                  ✉️ Email OTP
                </button>

              </div>

              <div className="forgot-form-group">

                <label htmlFor="identifier">
                  {method === "MOBILE"
                    ? t("auth.registeredMobile")
                    : t("auth.registeredEmail")}
                </label>

                <input
                  id="identifier"
                  type={method === "MOBILE" ? "tel" : "email"}
                  value={identifier}
                  maxLength={method === "MOBILE" ? 10 : 254}
                  inputMode={
                    method === "MOBILE" ? "numeric" : "email"
                  }
                  placeholder={
                    method === "MOBILE"
                      ? "9876543210"
                      : "example@gmail.com"
                  }
                  onChange={(event) => {
                    const value =
                      method === "MOBILE"
                        ? event.target.value.replace(/\D/g, "")
                        : event.target.value;

                    setIdentifier(value);
                  }}
                  autoComplete={
                    method === "MOBILE" ? "tel" : "email"
                  }
                  required
                  disabled={loading}
                />

              </div>

              <button
                type="submit"
                className="forgot-submit"
                disabled={loading}
              >
                {loading ? "Sending OTP..." : "Send OTP"}
              </button>

            </form>

          )}

          {/* ==================================
              STEP 2: VERIFY OTP
          ================================== */}

          {step === "verify" && (

            <form onSubmit={handleVerifyOtp}>

              <div className="otp-information">
                <span>📩</span>

                <p>
                  Enter the 6-digit OTP sent to your registered
                  {method === "MOBILE"
                    ? " mobile number."
                    : " email address."}
                </p>

                <strong>{identifier}</strong>
              </div>

              <div className="forgot-form-group">

                <label htmlFor="otp">
                  {t("auth.enterOtp")}
                </label>

                <input
                  id="otp"
                  type="text"
                  value={otp}
                  maxLength={6}
                  inputMode="numeric"
                  autoComplete="one-time-code"
                  placeholder="Enter 6-digit OTP"
                  onChange={(event) => {
                    setOtp(
                      event.target.value.replace(/\D/g, "")
                    );
                  }}
                  required
                  disabled={loading}
                />

              </div>

              <button
                type="submit"
                className="forgot-submit"
                disabled={loading}
              >
                {loading ? "Verifying..." : "Verify OTP"}
              </button>

              <div className="resend-section">

                <span>Didn't receive OTP?</span>

                <button
                  type="button"
                  onClick={handleResendOtp}
                  disabled={loading || resendIn > 0}
                >
                  {resendIn > 0
                    ? `Resend OTP in ${resendIn}s`
                    : "Resend OTP"}
                </button>

              </div>

              <button
                type="button"
                className="change-method"
                onClick={handleChangeMethod}
                disabled={loading}
              >
                ← Change mobile/email
              </button>

            </form>

          )}

          {/* ==================================
              STEP 3: RESET PASSWORD
          ================================== */}

          {step === "reset" && (

            <form onSubmit={handleResetPassword}>

              <div className="otp-information">
                <span>🔒</span>

                <p>
                  OTP verified successfully.
                  Please create your new password.
                </p>
              </div>

              <div className="forgot-form-group">

                <label htmlFor="newPassword">
                  {t("auth.newPassword")}
                </label>

                <div className="forgot-password-input">

                  <input
                    id="newPassword"
                    type={showPassword ? "text" : "password"}
                    value={newPassword}
                    placeholder="Enter new password"
                    autoComplete="new-password"
                    onChange={(event) =>
                      setNewPassword(event.target.value)
                    }
                    required
                    disabled={loading}
                  />

                  <button
                    type="button"
                    onClick={() =>
                      setShowPassword((previous) => !previous)
                    }
                    disabled={loading}
                    aria-label={
                      showPassword ? "Hide password" : "Show password"
                    }
                  >
                    {showPassword ? "🙈" : "👁️"}
                  </button>

                </div>

              </div>

              <div className="forgot-form-group">

                <label htmlFor="confirmPassword">
                  {t("auth.confirmNewPassword")}
                </label>

                <input
                  id="confirmPassword"
                  type="password"
                  value={confirmPassword}
                  placeholder="Confirm new password"
                  autoComplete="new-password"
                  onChange={(event) =>
                    setConfirmPassword(event.target.value)
                  }
                  required
                  disabled={loading}
                />

              </div>

              <div className="password-rules">
                Password must contain:

                <ul>
                  <li>At least 8 characters</li>
                  <li>One uppercase and one lowercase letter</li>
                  <li>One number</li>
                  <li>One special character</li>
                </ul>
              </div>

              <button
                type="submit"
                className="forgot-submit"
                disabled={loading}
              >
                {loading ? "Updating Password..." : "Reset Password"}
              </button>

            </form>

          )}

          {/* BACK TO LOGIN */}
          <div className="forgot-footer">

            <Link to="/login">
              ← {t("auth.backToLogin")}
            </Link>

          </div>

        </div>

        {/* PAGE FOOTER */}
        <div className="forgot-copyright">
          © {new Date().getFullYear()} {t("app.name")}
          <span>•</span>
          {t("app.tagline")}
        </div>

      </div>
    </div>
  );
};

export default ForgotPassword;
