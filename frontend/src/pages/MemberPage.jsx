import React, { useEffect, useState } from "react";
import { useParams, useSearchParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import jwt_decode from "jwt-decode"; // npm install jwt-decode
import "../styles/memberPage.css";

export default function MemberPage() {
  const { userId } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const [list, setList] = useState([]);
  const [userInfo, setUserInfo] = useState(null);
  const [activeTab, setActiveTab] = useState(searchParams.get("tab") || "articles");

  // JWT 토큰에서 로그인한 사용자 ID 추출
  let currentUserId = null;
  const token = localStorage.getItem("accessToken");
  if (token) {
    try {
      const decoded = jwt_decode(token);
      currentUserId = decoded.id;
    } catch (err) {
      console.error("Invalid token", err);
    }
  }

  // 🔹 유저 기본 정보 가져오기
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const res = await api.get(`/users/${userId}/info`);
        setUserInfo(res.data);
      } catch (err) {
        console.error("유저 정보 로드 실패:", err);
      }
    };
    fetchUserInfo();
  }, [userId]);

  // 🔹 게시글/댓글 등 목록 가져오기
  useEffect(() => {
    const tab = searchParams.get("tab") || "articles";
    setActiveTab(tab);

    const fetchData = async () => {
      try {
        let res;
        const isMe = currentUserId && Number(userId) === currentUserId;

        if (tab === "articles") {
          res = isMe ? await api.get(`/posts/me`) : await api.get(`/users/${userId}/posts`);
        } else if (tab === "comments") {
          res = isMe ? await api.get(`/comments/me`) : await api.get(`/comments/${userId}`);
        } else if (tab === "replied") {
          res = isMe
            ? await api.get(`/comments/replied/me`)
            : await api.get(`/users/${userId}/replied`);
        } else if (tab === "liked") {
          res = isMe ? await api.get(`/liked`) : await api.get(`/liked`);
        }

        setList(res.data || []);
      } catch (err) {
        console.error(err);
        setList([]);
      }
    };

    fetchData();
  }, [userId, searchParams, currentUserId]);

  const handleTabChange = (tab) => {
    setSearchParams({ tab });
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1100px", margin: "0 auto" }}>

      {/* 🔹 상단 프로필 카드 */}
      {userInfo && (
        <div
          style={{
            display: "flex",
            alignItems: "center",
            background: "#fff",
            border: "1px solid #ddd",
            borderRadius: "12px",
            boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
            padding: "16px 20px",
            marginBottom: "25px",
          }}
        >
          <img
            src={userInfo.profileImageUrl || "/default-profile.png"}
            alt="프로필"
            style={{
              width: "80px",
              height: "80px",
              borderRadius: "50%",
              objectFit: "cover",
              border: "1px solid #ddd",
              marginRight: "20px",
            }}
          />
          <div style={{ flex: 1 }}>
            <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
              <span style={{ fontSize: "1.3rem", fontWeight: "bold" }}>
                {userInfo.userId}
              </span>
              <span
                style={{
                  fontSize: "0.9rem",
                  color: "#666",
                  background: "#f5f5f5",
                  padding: "3px 10px",
                  borderRadius: "10px",
                }}
              >
                {userInfo.grade || "일반회원"}
              </span>
            </div>
            <div style={{ marginTop: "6px", color: "#777", fontSize: "0.9rem" }}>
              작성글: {userInfo.postCount || 0} | 댓글: {userInfo.commentCount || 0} | 가입일:{" "}
              {userInfo.joinDate ? new Date(userInfo.joinDate).toLocaleDateString() : "-"}
            </div>
          </div>
        </div>
      )}

      {/* 🔹 탭 메뉴 */}
      <div
        style={{
          display: "flex",
          borderBottom: "2px solid #ccc",
          marginBottom: "10px",
        }}
      >
        {["articles", "comments", "replied", "liked"].map((tab) => (
          <div
            key={tab}
            onClick={() => handleTabChange(tab)}
            style={{
              flex: 1,
              textAlign: "center",
              padding: "10px",
              cursor: "pointer",
              fontWeight: activeTab === tab ? "bold" : "normal",
              borderBottom: activeTab === tab ? "2px solid black" : "none",
            }}
          >
            {tab === "articles"
              ? "작성글"
              : tab === "comments"
              ? "작성댓글"
              : tab === "replied"
              ? "댓글단 글"
              : "좋아요한 글"}
          </div>
        ))}
      </div>

      {/* 🔹 리스트 */}
      {activeTab === "articles" || activeTab === "liked" || activeTab === "replied" ? (
  <div>
    {list.length === 0 ? (
      <p className="member-empty">불러올 내용이 없습니다.</p>
    ) : (
      <table className="member-table">
        <thead>
          <tr>
            <th>제목</th>
            <th>작성자</th>
            <th>댓글수</th>
            <th>조회수</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {list.map((item) => (
            <tr
              key={item.id}
              style={{ cursor: "pointer" }}
              onClick={() => navigate(`/${item.categoryName}/posts/${item.id}`)}
            >
              <td>
                <strong>[{item.categoryName}]</strong> {item.title}
              </td>
              <td>{item.writer}</td>
              <td style={{ textAlign: "center" }}>{item.commentCount}</td>
              <td style={{ textAlign: "center" }}>{item.viewCount}</td>
              <td>{item.createdAt?.slice(0, 10) || "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    )}
  </div>
) : (
  // 🔹 댓글 탭도 테이블 형식으로 맞추기
  <div>
    {list.length === 0 ? (
      <p className="member-empty">불러올 내용이 없습니다.</p>
    ) : (
      <table className="member-table">
        <thead>
          <tr>
            <th>댓글 내용</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {list.map((item, idx) => (
            <tr key={item.id || idx}>
              <td>{item.content}</td>
              <td style={{ textAlign: "center" }}>
                {item.createdAt?.slice(0, 10) || "-"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    )}
  </div>
)}
    </div>
  );
}
