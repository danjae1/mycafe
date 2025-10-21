import React, { useEffect, useState } from "react";
import { Link, useParams, useSearchParams } from "react-router-dom";
import api from "../api/api";
import Pagination from "../common/Pagination";

export default function PostList({ setShowBanner }) {
  const { categoryPath } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();

  const [posts, setPosts] = useState([]);
  const [categoryName, setCategoryName] = useState("");
  const [currentPage, setCurrentPage] = useState(Number(searchParams.get("page")) || 1);
  const [totalPages, setTotalPages] = useState(1);

  const pageSize = 10; // 한 페이지당 게시글 수
  const pageGroupSize = 5; // 페이지 버튼 그룹 크기

  // ✅ 카테고리 변경 시 페이지 초기화
  useEffect(() => {
    setCurrentPage(1);
    setSearchParams({ page: 1 });
  }, [categoryPath]);

  // 게시글 목록 조회
  useEffect(() => {
    const url = categoryPath
      ? `/posts?categoryPath=${categoryPath}&pageNum=${currentPage}&pageSize=${pageSize}`
      : `/posts?pageNum=${currentPage}&pageSize=${pageSize}`;

    setShowBanner(false);

    api
      .get(url)
      .then((res) => {
        const data = res.data;
        setPosts(data.content || []);
        setCategoryName(data.categoryName || categoryPath || "게시판");
        setTotalPages(data.totalPageCount || 1);
      })
      .catch((err) => {
        console.error("게시글 목록 조회 오류:", err);
        setPosts([]);
      });

    return () => setShowBanner(true);
  }, [categoryPath, currentPage, setShowBanner]);

  // 페이지 그룹 계산
  const groupIndex = Math.floor((currentPage - 1) / pageGroupSize);
  const startPageNum = groupIndex * pageGroupSize + 1;
  const endPageNum = Math.min(startPageNum + pageGroupSize - 1, totalPages);

  // 페이지 이동 핸들러
  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
    setSearchParams({ page });
  };

  return (
    <div style={{ padding: "20px" }}>
      <h3>{categoryName}</h3>

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr>
            <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>글번호</th>
            <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>제목</th>
            <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>작성자</th>
            <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>작성일</th>
            <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>조회수</th>
            <th style={{ borderBottom: "1px solid #ccc", padding: "8px" }}>좋아요</th>
          </tr>
        </thead>
        <tbody>
          {posts.length === 0 ? (
            <tr>
              <td colSpan={6} style={{ textAlign: "center", padding: "10px" }}>
                등록된 글이 없습니다.
              </td>
            </tr>
          ) : (
            posts.map((post) => (
              <tr key={post.id}>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>{post.id}</td>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>
                  <Link
                    to={`/${categoryPath || ""}/posts/${post.id}`}
                    style={{ color: "#333", textDecoration: "none" }}
                  >
                    {post.title}
                  </Link>
                </td>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>{post.writer}</td>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>{post.createdAt}</td>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>{post.viewCount}</td>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>{post.likeCount}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        startPageNum={startPageNum}
        endPageNum={endPageNum}
        onPageChange={handlePageChange}
      />
    </div>
  );
}
