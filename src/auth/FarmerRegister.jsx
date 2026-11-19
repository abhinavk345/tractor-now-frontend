
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";

import "./Register.css";

import { useLanguage } from "../context/LanguageContext";
import { registerFarmer } from "../services/AuthService";

const FarmerRegister = () => {
  const { t } = useLanguage();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: "",
    mobile: "",
    email: "",
    password: "",
    confirmPassword: "",
    village: "",
    district: "",
    state: "",
    pincode: "",
  });

  const [termsAccepted, setTermsAccepted] = useState(false);
  const [loading, setLoading] = useState(false);
  const [locationLoading, setLocationLoading] = useState(false);

  const [latitude, setLatitude] = useState(null);
  const [longitude, setLongitude] = useState(null);

  // HANDLE INPUT CHANGES
  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // GET CURRENT LOCATION AND ADDRESS
  const handleCurrentLocation1 = () => {
    if (!navigator.geolocation) {
      Swal.fire({
        icon: "error",
        title: "Location Not Supported",
        text: "Your browser does not support location services.",
      });
      return;
    }

    setLocationLoading(true);

    navigator.geolocation.getCurrentPosition(
      async (position) => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        // Save GPS coordinates
        setLatitude(lat);
        setLongitude(lon);

        try {
          const url = new URL(
            "https://nominatim.openstreetmap.org/reverse"
          );

          url.search = new URLSearchParams({
            lat: String(lat),
            lon: String(lon),
            format: "jsonv2",
            addressdetails: "1",
            "accept-language": "en",
          }).toString();

          const response = await fetch(url.toString());

          if (!response.ok) {
            throw new Error("Address lookup failed");
          }

          const data = await response.json();
          const address = data.address || {};

          const village =
            address.village ||
            address.town ||
            address.hamlet ||
            address.city ||
            address.suburb ||
            address.neighbourhood ||
            "";

          const district =
            address.state_district ||
            address.county ||
            address.district ||
            "";

          const state = address.state || "";

          const pincode = address.postcode
            ? address.postcode.replace(/\D/g, "").slice(0, 6)
            : "";

          // Automatically fill form fields
          setForm((previous) => ({
            ...previous,
            village,
            district,
            state,
            pincode,
          }));

          Swal.fire({
            icon: "success",
            title: "Location Detected!",
            html: `
              <div style="text-align:left">
                <p><b>Village:</b> ${village || "Not found"}</p>
                <p><b>District:</b> ${district || "Not found"}</p>
                <p><b>State:</b> ${state || "Not found"}</p>
                <p><b>Pincode:</b> ${pincode || "Not found"}</p>
              </div>
            `,
            confirmButtonColor: "#2e7d32",
          });

        } catch (error) {
          console.error("Address lookup error:", error);

          Swal.fire({
            icon: "warning",
            title: "Address Lookup Failed",
            text: "GPS coordinates were captured. Please enter your address manually.",
            confirmButtonColor: "#2e7d32",
          });

        } finally {
          setLocationLoading(false);
        }
      },

      (error) => {
        setLocationLoading(false);

        let message = "Unable to retrieve your location.";

        if (error.code === 1) {
          message = "Please allow location permission in your browser.";
        } else if (error.code === 2) {
          message = "Your current location is unavailable.";
        } else if (error.code === 3) {
          message = "Location request timed out. Please try again.";
        }

        Swal.fire({
          icon: "error",
          title: "Location Error",
          text: message,
          confirmButtonColor: "#d33",
        });
      },

      {
        enableHighAccuracy: true,
        timeout: 15000,
        maximumAge: 0,
      }
    );
  };

const handleCurrentLocation = () => {
  if (!navigator.geolocation) {
    Swal.fire({
      icon: "error",
      title: "Location Not Supported",
      text: "Your browser does not support location services.",
    });
    return;
  }

  Swal.fire({
    title: "Fetching Location...",
    text: "Please allow location access.",
    allowOutsideClick: false,
    didOpen: () => {
      Swal.showLoading();
    },
  });

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const lat = position.coords.latitude;
      const lng = position.coords.longitude;

      // 1. Save latitude and longitude
      setLatitude(lat);
      setLongitude(lng);

      try {
        // 2. Convert coordinates into address
        const response = await fetch(
          `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`
        );

        if (!response.ok) {
          throw new Error("Unable to fetch address");
        }

        const data = await response.json();
        const address = data.address || {};

        // 3. Automatically fill address fields
        setForm((previous) => ({
          ...previous,

          village:
            address.village ||
            address.town ||
            address.suburb ||
            address.hamlet ||
            address.city ||
            previous.village,

          district:
            address.state_district ||
            address.county ||
            previous.district,

          state:
            address.state ||
            previous.state,

          pincode:
            address.postcode ||
            previous.pincode,
        }));

        // 4. Success popup
        await Swal.fire({
          icon: "success",
          title: "Location Captured!",
          html: `
            <p>Latitude: ${lat}</p>
            <p>Longitude: ${lng}</p>
            <p>Address fields have been updated.</p>
          `,
          confirmButtonColor: "#2e7d32",
        });

      } catch (error) {
        console.error("Reverse geocoding error:", error);

        Swal.fire({
          icon: "warning",
          title: "Coordinates Captured",
          text: "Latitude and longitude were captured, but address lookup failed. Please enter your address manually.",
        });
      }
    },

    (error) => {
      let message = "";

      switch (error.code) {
        case 1:
          message = "Please allow location permission in browser settings.";
          break;

        case 2:
          message = "Location is currently unavailable.";
          break;

        case 3:
          message = "Location request timed out. Please try again.";
          break;

        default:
          message = "Unable to fetch your location.";
      }

      Swal.fire({
        icon: "error",
        title: "Location Error",
        text: message,
      });
    },

    {
      enableHighAccuracy: true,
      timeout: 20000,
      maximumAge: 0,
    }
  );
};



  // FARMER REGISTRATION
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.mobile.length !== 10) {
      Swal.fire({
        icon: "warning",
        title: "Invalid Mobile Number",
        text: "Please enter a valid 10-digit mobile number.",
      });
      return;
    }

    if (form.pincode.length !== 6) {
      Swal.fire({
        icon: "warning",
        title: "Invalid Pincode",
        text: "Please enter a valid 6-digit pincode.",
      });
      return;
    }

    if (form.password !== form.confirmPassword) {
      Swal.fire({
        icon: "warning",
        title: "Password Mismatch",
        text: t("validation.passwordMismatch"),
      });
      return;
    }

    if (!termsAccepted) {
      Swal.fire({
        icon: "warning",
        title: "Terms Required",
        text: t("validation.acceptTerms"),
      });
      return;
    }

    try {
      setLoading(true);

      const farmerData = {
        fullName: form.fullName,
        mobile: form.mobile,
        email: form.email,
        password: form.password,
        confirmPassword: form.confirmPassword,
        village: form.village,
        district: form.district,
        state: form.state,
        pincode: form.pincode,
        latitude: latitude,
        longitude: longitude,
        termsAccepted: termsAccepted,
      };

      const response = await registerFarmer(farmerData);

      console.log("Registration response:", response);

      await Swal.fire({
        icon: "success",
        title: "Registration Successful!",
        text:
          response.message ||
          "Your farmer account has been created successfully.",
        confirmButtonText: "Go to Login",
        confirmButtonColor: "#2e7d32",
        allowOutsideClick: false,
      });

      navigate("/login");

    } catch (error) {
      console.error("Registration error:", error);

      Swal.fire({
        icon: "error",
        title: "Registration Failed!",
        text:
          error.message ||
          error.error ||
          "Something went wrong. Please try again.",
        confirmButtonColor: "#d33",
      });

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-page">

      <div className="register-container">

        {/* HEADER */}

        <div className="register-brand">

          <div className="register-logo">
            👨‍🌾
          </div>

          <h1>{t("app.name")}</h1>

          <p>{t("auth.registerAsFarmer")}</p>

        </div>

        {/* CARD */}

        <div className="register-card">

          <div className="register-heading">

            <h2>{t("auth.registerAsFarmer")}</h2>

            <p>{t("home.welcome")}</p>

          </div>

          <form onSubmit={handleSubmit}>

            {/* PERSONAL INFORMATION */}

            <div className="section-title">
              {t("auth.fullName")}
            </div>

            <div className="form-group">

              <label htmlFor="fullName">
                {t("auth.fullName")} *
              </label>

              <input
                id="fullName"
                type="text"
                name="fullName"
                value={form.fullName}
                onChange={handleChange}
                placeholder={t("auth.fullName")}
                autoComplete="name"
                required
              />

            </div>

            {/* MOBILE AND EMAIL */}

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
                    const value = e.target.value.replace(/\D/g, "");

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

            {/* PASSWORD */}

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

            {/* LOCATION */}

            <div className="section-title location-title">
              📍 {t("location.location")}
            </div>

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
                    const value = e.target.value.replace(/\D/g, "");

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

            {/* CURRENT LOCATION */}

            <button
              type="button"
              className="location-button"
              onClick={handleCurrentLocation}
              disabled={locationLoading}
            >
              {locationLoading
                ? "📍 Detecting Location..."
                : `📍 ${t("location.useCurrentLocation")}`}
            </button>

            {latitude !== null && longitude !== null && (
              <div className="location-success">

                <p>✓ Location captured successfully</p>

                <small>Latitude: {latitude}</small>

                <small>Longitude: {longitude}</small>

              </div>
            )}

            {/* TERMS */}

            <div className="terms-container">

              <input
                type="checkbox"
                id="farmerTerms"
                checked={termsAccepted}
                onChange={(e) => setTermsAccepted(e.target.checked)}
                required
              />

              <label htmlFor="farmerTerms">
                {t("auth.acceptTerms")}
              </label>

            </div>

            {/* SUBMIT */}

            <button
              type="submit"
              className="register-button"
              disabled={loading}
            >
              {loading
                ? "Registering..."
                : t("auth.register")}
            </button>

          </form>

          {/* LOGIN */}

          <div className="already-account">

            <span>{t("auth.alreadyHaveAccount")}</span>

            <Link to="/login">
              {t("auth.login")}
            </Link>

          </div>

        </div>

        {/* FOOTER */}

        <div className="register-footer">

          © {new Date().getFullYear()} {t("app.name")}

          <span> • </span>

          {t("app.tagline")}

        </div>

      </div>

    </div>
  );
};

export default FarmerRegister;
