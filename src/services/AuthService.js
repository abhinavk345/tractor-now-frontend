
import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

// FARMER login API
export const loginUser = async (loginData) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/auth/login`,
      loginData,
      {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      }
    );

    return response.data;
  } catch (error) {
    throw error.response?.data || {
      message: "Unable to connect to server.",
    };
  }
};

// FARMER REGISTRATION API
export const registerFarmer = async (farmerData) => {
  try {
    const response = await axios.post(
      `${API_BASE_URL}/auth/register/farmer`,
      farmerData,
      {
        headers: {
          "Content-Type": "application/json",
        },
        withCredentials: true,
      }
    );

    return response.data;
  } catch (error) {
    throw error.response?.data || {
      message: "Unable to connect to server. Please try again.",
    };
  }
};
