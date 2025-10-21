import React, { useEffect, useState } from "react";
import { useParams, useSearchParams, Link } from "react-router-dom";
import jwt_decode from "jwt-decode";
import api from "../api/api";
import Pagination from "../common/Pagination";

import "../styles/memberPage.css";

export default function MemberPage() {
  const { userId, categoryPath } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();

  const activeTab = searchParams.get("tab") || "articles";

  // 탭별 페이지 상태
  const [tabPages, setTabPages] = useState({
    articles: 1,
    comments: 1,
    replied: 1,
    liked: 1,
  });

  const [userInfo, setUserInfo] = useState(null);
  const [list, setList] = useState([]);
  const [pageInfo, setPageInfo] = useState({
    pageNum: 1,
    pageSize: 10,
    totalPageCount: 1,
  });

  // 로그인한 유저
  let currentUserId = null;
  const token = localStorage.getItem("accessToken");
  if (token) {
    try {
      const decoded = jwt_decode(token);
      currentUserId = decoded.id;
    } catch {}
  }
  const isMe = currentUserId && Number(userId) === currentUserId;

  // 유저 정보 로드
  useEffect(() => {
    api
      .get(`/users/${userId}/info`)
      .then((res) => setUserInfo(res.data))
      .catch((err) => console.error("유저 정보 로드 실패:", err));
  }, [userId]);

  // 데이터 fetch
  const fetchList = async (tab, pageNum = 1) => {
    const size = 10; // 항상 10개씩
    try {
      let res;
      if (tab === "articles") {
        res = isMe
          ? await api.get(`/posts/me?pageNum=${pageNum}&pageSize=${size}`)
          : await api.get(`/users/${userId}/posts?pageNum=${pageNum}&pageSize=${size}`);
      } else if (tab === "comments") {
        res = isMe
          ? await api.get(`/comments/me?pageNum=${pageNum}&pageSize=${size}`)
          : await api.get(`/comments/${userId}?pageNum=${pageNum}&pageSize=${size}`);
      } else if (tab === "replied") {
        res = isMe
          ? await api.get(`/comments/replied/me?pageNum=${pageNum}&pageSize=${size}`)
          : await api.get(`/users/${userId}/replied?pageNum=${pageNum}&pageSize=${size}`);
      } else if (tab === "liked") {
        res = isMe
          ? await api.get(`/posts/liked/me?pageNum=${pageNum}&pageSize=${size}`)
          : await api.get(`/users/${userId}/posts/liked?pageNum=${pageNum}&pageSize=${size}`);
      }

      const data = res.data;
      setList(data.content || data || []);
      setPageInfo({
        pageNum: data.pageNum || pageNum,
        pageSize: data.pageSize || size,
        totalPageCount: data.totalPageCount || 1,
      });
    } catch (err) {
      console.error(err);
      setList([]);
      setPageInfo((prev) => ({ ...prev, totalPageCount: 1 }));
    }
  };

  // 탭 또는 페이지 변경 시 fetch
  useEffect(() => {
    const pageNum = tabPages[activeTab] || 1;
    fetchList(activeTab, pageNum);
  }, [activeTab, tabPages]);

  // 페이지 버튼 클릭
  const handlePageChange = (pageNum) => {
    setTabPages((prev) => ({ ...prev, [activeTab]: pageNum }));
  };

  // 탭 클릭
  const handleTabChange = (tab) => {
    setSearchParams({ tab });
    if (!tabPages[tab]) {
      setTabPages((prev) => ({ ...prev, [tab]: 1 }));
    }
  };

  // 페이지 그룹 계산
  const pageGroupSize = 5;
  const groupIndex = Math.floor((pageInfo.pageNum - 1) / pageGroupSize);
  const startPageNum = groupIndex * pageGroupSize + 1;
  const endPageNum = Math.min(startPageNum + pageGroupSize - 1, pageInfo.totalPageCount);

  const getItemLink = (item) => {
    if (["articles", "liked", "replied"].includes(activeTab)) {
      // categoryName에서 path 추출 또는 기본값 사용
      const path = item.categoryPath || "/free"; // 기본값으로 /free 사용
      return `${path}/posts/${item.id}`;
    }
    if (activeTab === "comments") {
      // 댓글의 경우 postId를 사용
      const path = item.categoryPath || "/free";
      return `${path}/posts/${item.postId}`;
    }
    return "#";
  };

  return (
    <div className="member-page">
      {/* 프로필 카드 */}
      {userInfo && (
        <div className="member-profile">
          <img src={userInfo.profileImageUrl || "/default-profile.png"} alt="프로필" />
          <div className="profile-info">
            <h2>{userInfo.userId}</h2>
            <p>
              작성글 {userInfo.postCount || 0}개 · 댓글 {userInfo.commentCount || 0}개
            </p>
          </div>
        </div>
      )}

      {/* 탭 */}
      <div className="member-tabs">
        <ul className="tab-list">
          {["articles", "comments", "replied", "liked"].map((tab) => (
            <li
              key={tab}
              className={`tab-item ${activeTab === tab ? "active" : ""}`}
              onClick={() => handleTabChange(tab)}
            >
              {tab === "articles"
                ? "작성글"
                : tab === "comments"
                ? "작성한 댓글"
                : tab === "replied"
                ? "댓글단 글"
                : "좋아요한 글"}
            </li>
          ))}
        </ul>
      </div>

      {/* 리스트 */}
      <table className={`member-table ${activeTab === "comments" ? "comments" : ""}`}>
        <thead>
          <tr>
            <th>글번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
            <th>좋아요</th>
          </tr>
        </thead>
        <tbody>
          {list.length === 0 ? (
            <tr>
              <td colSpan={6} className="member-empty">
                등록된 글이 없습니다.
              </td>
            </tr>
          ) : (
            list.map((item) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>
                  <Link to={getItemLink(item)} style={{ color: "#333", textDecoration: "none" }}>
                    {item.title || item.content || "-"}
                  </Link>
                </td>
                <td>{item.writer || item.userId || "-"}</td>
                <td>{item.createdAt || "-"}</td>
                <td>{item.viewCount ?? "-"}</td>
                <td>{item.likeCount ?? "-"}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {/* 페이지네이션 */}
      <Pagination
        currentPage={pageInfo.pageNum}
        totalPages={pageInfo.totalPageCount}
        startPageNum={startPageNum}
        endPageNum={endPageNum}
        onPageChange={handlePageChange}
      />
    </div>
  );
}
