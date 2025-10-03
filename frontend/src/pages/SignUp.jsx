// Signup.jsx
import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";
import axios from "axios";

export default function Signup({ show,onSignupSuccess, onCancel }) {
  if(!show) return null;
  
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await axios.post("/api/signup", {
        username: name,
        email: email, // 백엔드 DTO에서 username이 이메일이면 이렇게 전달
        password: password,
        grade:"BRONZE"
      });

      console.log("회원가입 성공:", response.data);
      alert("회원가입이 완료되었습니다.");
      onSignupSuccess(); // 성공 시 모달 닫기
    } catch (error) {
      if (error.response && error.response.data?.error) {
        alert("회원가입 실패: " + error.response.data.error);
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

        <h2 style={{ marginBottom: "1.5rem", color: "#333" }}>회원가입</h2>

        <Form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: "1rem" }}
        >
          <Form.Control
            type="text"
            placeholder="이름"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
          <Form.Control
            type="email"
            placeholder="이메일"
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
              backgroundColor: "blue",
              border: "none",
              fontSize: "1rem",
              padding: "0.5rem",
            }}
            disabled={loading}
          >
            {loading ? "회원가입 중..." : "회원가입"}
          </Button>
        </Form>
      </div>
    </div>
  );
}
