import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import "./Register.css";

import { useLanguage } from "../context/LanguageContext";

const OwnerRegister = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    ownerName: "",
    mobile: "",
    email: "",
    password: "",
    confirmPassword: "",
    village: "",
    district: "",
    state: "",
    pincode: "",
    ownerType: "INDIVIDUAL",
  });

  const [termsAccepted, setTermsAccepted] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
      alert(t("validation.passwordMismatch"));
      return;
    }

    if (!termsAccepted) {
      alert(t("validation.acceptTerms"));
      return;
    }

    setLoading(true);

    console.log("Owner Registration:", form);

    // TODO:
    // Connect Spring Boot registration API here.
    // OTP verification will be added here.

    setTimeout(() => {
      setLoading(false);

      alert(t("register.ownerRegistrationSuccessful"));

      navigate("/login");
    }, 800);
  };

  return (
    <div className="register-page">

      <div className="register-container">

        {/* =========================
            HEADER
        ========================= */}

        <div className="register-brand">

          <div className="register-logo owner-logo">
            🚜
          </div>

          <h1>
            {t("app.name")}
          </h1>

          <p>
            {t("auth.registerAsOwner")}
          </p>

        </div>

        {/* =========================
            CARD
        ========================= */}

        <div className="register-card">

          <div className="register-heading">

            <h2>
              {t("register.ownerTitle")}
            </h2>

            <p>
              {t("register.ownerDescription")}
            </p>

          </div>

          <form onSubmit={handleSubmit}>

            {/* =========================
                OWNER INFORMATION
            ========================= */}

            <div className="section-title">
              {t("register.ownerInformation")}
            </div>

            {/* Owner Name */}

            <div className="form-group">

              <label htmlFor="ownerName">
                {t("auth.ownerName")} *
              </label>

              <input
                id="ownerName"
                type="text"
                name="ownerName"
                value={form.ownerName}
                onChange={handleChange}
                placeholder={t("auth.ownerName")}
                autoComplete="name"
                required
              />

            </div>

            {/* =========================
                OWNER TYPE
            ========================= */}

            <div className="form-group">

              <label>
                {t("register.ownerType")} *
              </label>

              <div className="owner-type-container">

                {/* Individual */}

                <label
                  className={`owner-type ${
                    form.ownerType === "INDIVIDUAL"
                      ? "selected"
                      : ""
                  }`}
                >

                  <input
                    type="radio"
                    name="ownerType"
                    value="INDIVIDUAL"
                    checked={
                      form.ownerType === "INDIVIDUAL"
                    }
                    onChange={handleChange}
                  />

                  <span>
                    👤 {t("register.individualOwner")}
                  </span>

                </label>

                {/* Business */}

                <label
                  className={`owner-type ${
                    form.ownerType === "BUSINESS"
                      ? "selected"
                      : ""
                  }`}
                >

                  <input
                    type="radio"
                    name="ownerType"
                    value="BUSINESS"
                    checked={
                      form.ownerType === "BUSINESS"
                    }
                    onChange={handleChange}
                  />

                  <span>
                    🏢 {t("register.equipmentBusiness")}
                  </span>

                </label>

              </div>

            </div>

            {/* =========================
                CONTACT
            ========================= */}

            <div className="form-row">

              <div className="form-group">

                <label htmlFor="mobile">
                  {t("auth.mobile")} *
                </label>

                <input
                  id="mobile"
                  type="tel"
                  name="mobile"
                  value={form.mobile}
                  onChange={(e) => {
                    const value =
                      e.target.value.replace(/\D/g, "");

                    setForm((previous) => ({
                      ...previous,
                      mobile: value,
                    }));
                  }}
                  placeholder="9876543210"
                  maxLength={10}
                  pattern="[0-9]{10}"
                  inputMode="numeric"
                  autoComplete="tel"
                  required
                />

              </div>

              <div className="form-group">

                <label htmlFor="email">
                  {t("auth.email")}
                </label>

                <input
                  id="email"
                  type="email"
                  name="email"
                  value={form.email}
                  onChange={handleChange}
                  placeholder="example@email.com"
                  autoComplete="email"
                />

              </div>

            </div>

            {/* =========================
                PASSWORD
            ========================= */}

            <div className="form-row">

              <div className="form-group">

                <label htmlFor="password">
                  {t("auth.password")} *
                </label>

                <input
                  id="password"
                  type="password"
                  name="password"
                  value={form.password}
                  onChange={handleChange}
                  placeholder="••••••••"
                  minLength={6}
                  autoComplete="new-password"
                  required
                />

              </div>

              <div className="form-group">

                <label htmlFor="confirmPassword">
                  {t("auth.confirmPassword")} *
                </label>

                <input
                  id="confirmPassword"
                  type="password"
                  name="confirmPassword"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  placeholder="••••••••"
                  minLength={6}
                  autoComplete="new-password"
                  required
                />

              </div>

            </div>

            {/* =========================
                LOCATION
            ========================= */}

            <div className="section-title location-title">
              📍 {t("register.equipmentLocation")}
            </div>

            {/* Village + District */}

            <div className="form-row">

              <div className="form-group">

                <label htmlFor="village">
                  {t("location.village")} *
                </label>

                <input
                  id="village"
                  type="text"
                  name="village"
                  value={form.village}
                  onChange={handleChange}
                  placeholder={t("location.village")}
                  required
                />

              </div>

              <div className="form-group">

                <label htmlFor="district">
                  {t("location.district")} *
                </label>

                <input
                  id="district"
                  type="text"
                  name="district"
                  value={form.district}
                  onChange={handleChange}
                  placeholder={t("location.district")}
                  required
                />

              </div>

            </div>

            {/* State + Pincode */}

            <div className="form-row">

              <div className="form-group">

                <label htmlFor="state">
                  {t("location.state")} *
                </label>

                <input
                  id="state"
                  type="text"
                  name="state"
                  value={form.state}
                  onChange={handleChange}
                  placeholder={t("location.state")}
                  required
                />

              </div>

              <div className="form-group">

                <label htmlFor="pincode">
                  {t("location.pincode")} *
                </label>

                <input
                  id="pincode"
                  type="text"
                  name="pincode"
                  value={form.pincode}
                  onChange={(e) => {
                    const value =
                      e.target.value.replace(/\D/g, "");

                    setForm((previous) => ({
                      ...previous,
                      pincode: value,
                    }));
                  }}
                  placeholder="000000"
                  maxLength={6}
                  pattern="[0-9]{6}"
                  inputMode="numeric"
                  required
                />

              </div>

            </div>

            {/* =========================
                CURRENT LOCATION
            ========================= */}

            <button
              type="button"
              className="location-button"
              onClick={() =>
                alert(t("location.locationPermission"))
              }
            >
              📍 {t("register.useCurrentLocation")}
            </button>

            {/* =========================
                TERMS
            ========================= */}

            <div className="terms-container">

              <input
                type="checkbox"
                id="ownerTerms"
                checked={termsAccepted}
                onChange={(e) =>
                  setTermsAccepted(e.target.checked)
                }
                required
              />

              <label htmlFor="ownerTerms">
                {t("auth.acceptTerms")}
              </label>

            </div>

            {/* =========================
                SUBMIT
            ========================= */}

            <button
              type="submit"
              className="register-button"
              disabled={loading}
            >
              {loading
                ? t("register.creatingAccount")
                : t("register.createOwnerAccount")}
            </button>

          </form>

          {/* =========================
              LOGIN
          ========================= */}

          <div className="already-account">

            <span>
              {t("auth.alreadyHaveAccount")}
            </span>

            <Link to="/login">
              {t("auth.login")}
            </Link>

          </div>

        </div>

        {/* =========================
            FOOTER
        ========================= */}

        <div className="register-footer">

          © {new Date().getFullYear()} {t("app.name")}

          <span> • </span>

          {t("register.ownerFooter")}

        </div>

      </div>

    </div>
  );
};

export default OwnerRegister;