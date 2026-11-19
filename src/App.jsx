import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "./auth/Login";
import FarmerRegister from "./auth/FarmerRegister";
import OwnerRegister from "./auth/OwnerRegister";
import FarmerHome from "./pages/farmer/FarmerHome";
import ForgotPassword from "./pages/ForgotPassword";
function App() {
  return (
    <BrowserRouter>
      <Routes>

        <Route
          path="/"
          element={<Navigate to="/login" replace />}
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
          path="/forgot-password"
          element={<ForgotPassword />}
        />

        <Route
          path="/register/owner"
          element={<OwnerRegister />}
        />

        <Route
          path="/farmer/home"
          element={<FarmerHome />}
        />

        <Route
          path="/owner/dashboard"
          element={
            <div>Owner Dashboard</div>
          }
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;