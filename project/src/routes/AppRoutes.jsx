import React from "react";
import { Routes, Route } from "react-router-dom";

import Login from "../auth/Login";
import FarmerRegister from "../auth/FarmerRegister";
import OwnerRegister from "../auth/OwnerRegister";

import FarmerHome from "../pages/farmer/FarmerHome";

const AppRoutes = () => {
  return (
    <Routes>

      {/* Authentication */}

      <Route
        path="/"
        element={<Login />}
      />

      <Route
        path="/login"
        element={<Login />}
      />

      <Route
        path="/register/farmer"
        element={<FarmerRegister />}
      />

      <Route
        path="/register/owner"
        element={<OwnerRegister />}
      />

      {/* Farmer */}

      <Route
        path="/farmer/home"
        element={<FarmerHome />}
      />

      {/* Temporary */}

      <Route
        path="/owner/dashboard"
        element={<h1>Owner Dashboard</h1>}
      />

    </Routes>
  );
};

export default AppRoutes;