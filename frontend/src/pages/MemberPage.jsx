import React, { useEffect, useState } from "react";
import { useParams, useSearchParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import jwt_decode from "jwt-decode"; // npm install jwt-decode

export default function MemberPage() {
  const { userId } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const [list, setList] = useState([]);
  const [activeTab, setActiveTab] = useState(searchParams.get("tab") || "articles");

  // JWT 토큰에서 로그인한 사용자 ID 추출
  let currentUserId = null;
  const token = localStorage.getItem("accessToken"); // 혹은 sessionStorage
  if (token) {
    try {
      const decoded = jwt_decode(token);
      currentUserId = decoded.id; // 토큰 payload에서 id 필드 확인
    } catch (err) {
      console.error("Invalid token", err);
    }
  }

  useEffect(() => {
    const tab = searchParams.get("tab") || "articles";
    setActiveTab(tab);

    const fetchData = async () => {
      try {
        let res;
        // 본인 여부 판단
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
    <div style={{ padding: "20px" }}>
      {/* 탭 메뉴 */}
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

      {/* 리스트 */}
      {activeTab === "articles" || activeTab === "liked" || activeTab == "replied" ? (
        <div>
          {list.length === 0 ? (
            <p style={{ textAlign: "center", color: "#888" }}>불러올 내용이 없습니다.</p>
          ) : (
            <table
              style={{
                width: "100%",
                borderCollapse: "collapse",
                fontSize: "0.95rem",
              }}
            >
              <thead>
                <tr style={{ borderBottom: "2px solid #ddd", textAlign: "left" }}>
                  <th style={{ padding: "8px" }}>제목</th>
                  <th style={{ padding: "8px", width: "120px" }}>작성자</th>
                  <th style={{ padding: "8px", width: "80px" }}>댓글수</th>
                  <th style={{ padding: "8px", width: "80px" }}>조회수</th>
                  <th style={{ padding: "8px", width: "120px" }}>작성일</th>
                </tr>
              </thead>
              <tbody>
                {list.map((item) => (
                  <tr
                    key={item.id}
                    style={{
                      borderBottom: "1px solid #eee",
                      cursor: "pointer",
                    }}
                  >
                    <td style={{ padding: "8px" }} onClick={() => navigate(`/${item.categoryName}/posts/${item.id}`)}>
                      <strong>[{item.categoryName}]</strong> {item.title}
                    </td>
                    <td style={{ padding: "8px" }}>{item.writer}</td>
                    <td style={{ padding: "8px", textAlign: "center" }}>
                      {item.commentCount}
                    </td>
                    <td style={{ padding: "8px", textAlign: "center" }}>{item.viewCount}</td>
                    <td style={{ padding: "8px" }}>{item.createdAt?.slice(0, 10) || "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      ) : (
        <div>
          {list.length === 0 ? (
            <p style={{ textAlign: "center", color: "#888" }}>불러올 내용이 없습니다.</p>
          ) : (
            list.map((item, idx) => (
              <div
                key={item.id || idx}
                style={{
                  display: "grid",
                  gridTemplateColumns: "1fr auto",
                  borderBottom: "1px solid #eee",
                  padding: "8px 0",
                }}
              >
                <span >{item.content}</span>
                <span style={{ color: "#999", fontSize: "0.8rem" }}>
                  {item.createdAt?.slice(0, 10)}
                </span>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}
