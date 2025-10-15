import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";

export default function UserInfo() {
  const [userSummary, setUserSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/users/me")
      .then((res) => setUserSummary(res.data))
      .catch((err) => console.log("유저 요약 정보 가져오기 실패 : ", err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>로딩 중...</p>;
  if (!userSummary) return <p>정보를 불러올 수 없습니다.</p>;

  const containerStyle = {
    width: "100%",
    height: "200px", // 박스 고정 높이
    padding: "10px",
    fontFamily: "sans-serif",
    border: "1px solid #ccc",
    borderRadius: "8px",
    boxSizing: "border-box",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
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

  const linkStyle = {
    color: "#0078d4",
    textDecoration: "none",
  };

  return (
    <div style={containerStyle}>
      <div>
        <div style={titleStyle}>
          {/* <img
            src={userSummary.profileUrl || "https://via.placeholder.com/24"}
            alt="프로필"
            style={profileImageStyle} 
          /> */}
          <span>{userSummary.userId}</span>
        </div>
        <div style={{ fontSize: "0.75rem" }}>
          가입일:{" "}
          {userSummary.joinDate
            ? new Date(userSummary.joinDate).toLocaleDateString()
            : "-"}
        </div>
        <hr style={{ border: "0.5px solid #ccc", margin: "5px 0 10px 0" }} />

        <div style={infoRowStyle}>
          <span>등급</span>
          <span>{userSummary.grade}</span>
        </div>

        {/* 🔗 내가 쓴 글 */}
        <div style={infoRowStyle}>
          <Link to={`/members/${userSummary.userId}?tab=articles`} style={linkStyle}>내가 쓴 글</Link>
          <span>{userSummary.postCount}</span>
        </div>

        {/* 🔗 내가 쓴 댓글 */}
        <div style={infoRowStyle}>
          <Link to={`/members/${userSummary.userId}?tab=comments`} style={linkStyle}>내가 쓴 댓글</Link>
          <span>{userSummary.commentCount}</span>
        </div>

        {/* 🔗 좋아요한 글 */}
        <div style={infoRowStyle}>
          <Link to={`/members/${userSummary.userId}?tab=liked`} style={linkStyle}>좋아요 누른 글</Link>
          <span>{userSummary.likedPostCount}</span>
        </div>
      </div>
    </div>
  );
}
