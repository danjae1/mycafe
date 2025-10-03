import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";
import api from "../api/api";
// axios 인터셉터 적용된 api import

export default function Login({ show, onLoginSuccess, onCancel }) {
  if (!show) return null;

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post("/login", {
        username: email,
        password: password,
      });

      const { accessToken, expiresIn } = response.data;

      // 로컬스토리지에 저장
      localStorage.setItem("accessToken", accessToken.trim());
      localStorage.setItem("expiresIn", expiresIn);

      console.log("로그인 성공, 토큰 만료시간:", expiresIn, "초");
      onLoginSuccess(accessToken); // 모달 닫고 홈으로 이동
    } catch (error) {
      if (error.response && error.response.data?.error) {
        alert("로그인 실패: " + error.response.data.error);
      } else {
        alert("네트워크 오류 발생");
      }
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        width: "100vw",
        height: "100vh",
        backgroundColor: "rgba(0,0,0,0.2)",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        zIndex: 1000,
      }}
    >
      <div
        style={{
          position: "relative",
          backgroundColor: "#fff",
          padding: "2.5rem 2rem",
          borderRadius: "12px",
          boxShadow: "0 8px 20px rgba(0,0,0,0.2)",
          width: "360px",
          maxWidth: "90%",
          textAlign: "center",
        }}
      >
        {/* 닫기 버튼 */}
        <Button
          variant="link"
          onClick={onCancel}
          style={{
            position: "absolute",
            top: "1rem",
            right: "1rem",
            fontSize: "1.2rem",
            textDecoration: "none",
            color: "#000",
            padding: 0,
          }}
        >
          &times;
        </Button>

        <h2 style={{ marginBottom: "1.5rem" }}>로그인</h2>

        <Form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "1rem" }}
        >
          <Form.Control
            type="text"
            placeholder="아이디"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <Form.Control
            type="password"
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <Button
            type="submit"
            style={{
              color: "white",
              backgroundColor: "green",
              border: "none",
              fontSize: "1rem",
              padding: "0.5rem",
            }}
            disabled={loading}
          >
            {loading ? "로그인 중..." : "로그인"}
          </Button>
        </Form>
      </div>
    </div>
  );
}
