
import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

// Common Axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true,
});

// Common error handler
const handleApiError = (error, defaultMessage) => {
  console.error("API Error:", error);

  if (error.response) {
    const status = error.response.status;
    const data = error.response.data;

    return new Error(
      data?.message ||
      data?.error ||
      (status === 401 || status === 403
        ? "Invalid credentials or unauthorized request."
        : defaultMessage)
    );
  }

  if (error.request) {
    return new Error(
      "Unable to connect to server. Please check your connection."
    );
  }

  return new Error(error.message || defaultMessage);
};

// ======================================
// LOGIN API
// ======================================

export const loginUser = async (loginData) => {
  try {
    const response = await api.post("/auth/login", loginData);
    return response.data;
  } catch (error) {
    throw handleApiError(error, "Unable to login. Please try again.");
  }
};

// ======================================
// FARMER REGISTRATION API
// ======================================

export const registerFarmer = async (farmerData) => {
  try {
    const response = await api.post(
      "/auth/register/farmer",
      farmerData
    );

    return response.data;
  } catch (error) {
    throw handleApiError(error, "Unable to register. Please try again.");
  }
};

// ======================================
// SEND PASSWORD RESET OTP
// ======================================

export const sendPasswordResetOtp = async (data) => {
  try {
    const response = await api.post(
      "/auth/forgot-password",
      {
        method: data.method,
        identifier: data.identifier,
      }
    );

    return response.data;
  } catch (error) {
    throw handleApiError(error, "Unable to send OTP.");
  }
};

// ======================================
// VERIFY PASSWORD RESET OTP
// ======================================

export const verifyPasswordResetOtp = async (data) => {
  try {
    const response = await api.post(
      "/auth/verify-reset-otp",
      {
        method: data.method,
        identifier: data.identifier,
        otp: data.otp,
      }
    );

    // Backend sets the HttpOnly reset-token cookie.
    // Do not store the token in localStorage or React state.

    return response.data;
  } catch (error) {
    throw handleApiError(error, "Invalid or expired OTP.");
  }
};

// ======================================
// RESET PASSWORD
// ======================================

export const resetPassword = async (data) => {
  try {
    const response = await api.post(
      "/auth/reset-password",
      {
        newPassword: data.newPassword,
        confirmPassword: data.confirmPassword,
      }
    );

    return response.data;
  } catch (error) {
    throw handleApiError(
      error,
      "Unable to reset password. Please try again."
    );
  }
};

export default api;
