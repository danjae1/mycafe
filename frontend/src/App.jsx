import React, { useEffect, useState } from "react";
import TopBanner from "./components/TopBanner";
import SideHeader from "./components/SideHeader";
import Sidebar from "./components/Sidebar";
import AppRouter from "./router";
import MainBanner from "./components/MainBanner";
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import Signup from "./pages/SignUp";
import api from "./api/api";
import "./styles/common.css";
import { useNavigate, useLocation } from "react-router-dom";

export default function App() {
  const [page, setPage] = useState("home"); // "home" | "login" | "signup"
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [accessToken, setAccessToken] = useState(localStorage.getItem("accessToken") || null);
  const [showBanner, setShowBanner] = useState(true);

  const navigate = useNavigate();
  const location = useLocation(); // 현재 경로 확인

  // 새로고침 시 서버에서 로그인 상태 확인
  useEffect(() => {
    let mounted = true;

    const checkLogin = async () => {
      try {
        if (!accessToken) {
          const res = await api.get("/check", { withCredentials: true });
          if (!mounted) return;
          setIsLoggedIn(res.data.isLoggedIn);
        } else {
          setIsLoggedIn(true);
        }
      } catch (err) {
        console.log("checkLogin 에러:", err);
        if (mounted) setIsLoggedIn(false);
      }
    };

    checkLogin();

    return () => { mounted = false };
  }, [accessToken]);

  // 로그아웃 처리
  const handleLogout = async () => {
    try {
      await api.post("/logout", {}, { withCredentials: true });
      setIsLoggedIn(false);
      setAccessToken(null);
      localStorage.removeItem("accessToken");
      navigate("/");
    } catch (err) {
      console.log(err);
    }
  };

  // 로그인 성공 처리
  const handleLoginSuccess = (token) => {
    setAccessToken(token);
    localStorage.setItem("accessToken", token);
    setIsLoggedIn(true);
    setPage("home");
  };

  // 회원가입 성공 처리
  const handleSignupSuccess = () => setPage("login");

  // 경로에 따라 MainBanner 보여줄지 결정
  useEffect(() => {
    if (location.pathname === "/") {
      setShowBanner(true); // 홈일 때 배너 보이기
    } else {
      setShowBanner(false); // 홈이 아니면 배너 숨기기
    }
  }, [location.pathname]);

  return (
    <div style={{ display: "flex", flexDirection: "column", minHeight: "100vh", boxSizing: "border-box" }}>
      {/* Navbar */}
      <div style={{ marginLeft: "7cm", marginRight: "7.5cm" }}>
        <Navbar
          isLoggedIn={isLoggedIn}
          onLogout={handleLogout}
          onLoginClick={() => setPage("login")}
          onSignupClick={() => setPage("signup")}
        />
      </div>

      {/* TopBanner */}
      <div style={{ marginLeft: "7cm", marginRight: "7.5cm", marginTop: "20px" }}>
        <TopBanner />
      </div>

      <div style={{ display: "flex", marginTop: "20px", gap: "20px" }}>
        {/* SideHeader + Sidebar */}
        <div style={{
          marginLeft: "7cm",
          flex: "0 0 220px",
          display: "flex",
          flexDirection: "column",
          gap: "20px",
          minHeight: "200vh",
          overflowY: "auto",
        }}>
          <SideHeader />
          <Sidebar />
        </div>

        {/* Main Content */}
        <div style={{
          flex: "1 1 auto",
          marginRight: "7.5cm",
          display: "flex",
          flexDirection: "column",
          gap: "20px",
        }}>
          {showBanner && (
            <div style={{
              width: "100%",
              height: "7cm",
              backgroundColor: "#f0f0f0",
              border: "1px solid #ccc",
              boxSizing: "border-box"
            }}>
              <MainBanner />
            </div>
          )}
          <AppRouter setShowBanner={setShowBanner} />
        </div>
      </div>

      {/* 로그인/회원가입 모달 */}
      <Login
        show={page === "login"}
        onLoginSuccess={handleLoginSuccess}
        onCancel={() => setPage("home")}
      />
      <Signup
        show={page === "signup"}
        onSignupSuccess={handleSignupSuccess}
        onCancel={() => setPage("home")}
      />
    </div>
  );
}
