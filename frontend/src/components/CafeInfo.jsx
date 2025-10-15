import React, { useEffect, useState } from "react";
import api from "../api/api";

export default function CafeInfo() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/cafe/summary")
      .then((res) => setSummary(res.data))
      .catch((err) => console.error("카페 통계 가져오기 실패:", err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>로딩 중...</p>;
  if (!summary) return <p>정보를 불러올 수 없습니다.</p>;

  const containerStyle = {
  width: "100%",
  height: "200px",          // 박스 고정 높이
  padding: "10px",
  fontFamily: "sans-serif",
  border: "1px solid #ccc",
  borderRadius: "8px",
  boxSizing: "border-box",
  display: "flex",
  flexDirection: "column",
  justifyContent: "space-between", // 내용 상단/하단 배치
  lineHeight: 1.4,
  fontSize: "0.85rem",
};


  const titleStyle = {
    fontWeight: "bold",
    fontSize: "0.95rem",
    marginBottom: "5px",
    display: "flex",
    alignItems: "center",
  };

  const infoRowStyle = {
    display: "flex",
    justifyContent: "space-between",
    marginBottom: "6px",
    wordBreak: "break-word",
  };
  const profileImageStyle = {
    width: "24px",
    height: "24px",
    borderRadius: "50%",
    objectFit: "cover",
    marginRight: "8px",
  };
  
  return (
  <div style={containerStyle}>
    <div>
      <div style={titleStyle}>
        <img
          src={summary.profileUrl || "https://via.placeholder.com/24"}
          alt="프로필"
          style={profileImageStyle}
        />
        <span>임채호 카페</span>
      </div>
      <hr style={{ border: "0.5px solid #ccc", margin: "5px 0 10px 0" }} />
      <div style={infoRowStyle}>
        <span>회원 수</span>
        <span>{summary.userCount}명</span>
      </div>
      <div style={infoRowStyle}>
        <span>전체 글 수</span>
        <span>{summary.postCount}개</span>
      </div>
      <div style={infoRowStyle}>
        <span>전체 댓글 수</span>
        <span>{summary.commentCount}개</span>
      </div>
    </div>
    {/* 여백 또는 버튼 등 아래 고정 영역이 필요하면 여기 추가 */}
  </div>
);

}
