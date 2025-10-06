import React from "react";
import { useNavigate } from "react-router-dom";

export default function WriteButton() {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate("/write"); // 글쓰기 페이지로 이동
  };

  return (
    <button
      onClick={handleClick}
      style={{
        width: "100%",
        padding: "10px",
        backgroundColor: "#333",
        color: "#fff",
        border: "none",
        borderRadius: "6px",
        cursor: "pointer",
        marginTop: "8px",
      }}
    >
      ✏️ 글쓰기
    </button>
  );
}
