// components/SideHeader.jsx
import React, { useState } from "react";
import CafeInfo from "./CafeInfo";
import UserInfo from "./UserInfo";
import WriteButton from "./WriteButton";

export default function SideHeader() {
  const [activeTab, setActiveTab] = useState("cafe"); // 기본 MyCafe
  
  return (
    <div
      style={{
        width: "220px",
        height: "auto",
        boxSizing: "border-box",
        borderBottom: "1px solid grey",
        backgroundColor: "#fff",
        borderRadius: "8px",
        boxShadow: "0 0 5px rgba(0,0,0,0.1)",
        display: "flex",
        flexDirection: "column",
        gap: "8px",
        padding: "10px",
        overflow: "hidden", // 가로 스크롤 방지
      }}
    >
      {/* 상단 탭 */}
      <div style={{ display: "flex", justifyContent: "space-between",
        borderBottom: "1px solid #ccc" ,borderTop: "1px solid black" }}>
        <div
          onClick={() => setActiveTab("cafe")}
          style={{
            cursor: "pointer",
            fontWeight: activeTab === "cafe" ? "bold" : "normal",
            flex:1,
            textAlign : "center",
            borderRight: "1px solid black",
            padding: "5px 0",
          }}
        >
          카페 정보
        </div>
        <div
          onClick={() => setActiveTab("user")}
          style={{
            cursor: "pointer",
            fontWeight: activeTab === "user" ? "bold" : "normal",
            flex : 1,
            textAlign : "center",
            padding: "5px 0",
          }}
        >
          내 정보
        </div>
      </div>

      {/* 선택된 탭 내용 */}
      <div
        style={{
          marginTop: "8px",
          maxHeight: "calc(100vh - 13cm - 60px)", // 상단 + 탭 높이 제외
          overflowY: "auto",
        }}
      >
        {activeTab === "cafe" && <CafeInfo />}
        {activeTab === "user" && <UserInfo />}
      </div>

      {/* 아래 버튼 */}
       <div style={{ marginTop: "auto" }}>
        <WriteButton />
    
        </div>
    </div>
  );
}
