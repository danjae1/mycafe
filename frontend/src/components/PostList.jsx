import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import api from "../api/api";

export default function PostList({setShowBanner}) {

    const { categoryPath } = useParams();
    const [posts, setPosts] = useState([]);
    const [categoryName, setCategoryName] = useState("");

  useEffect(() => {

    const url = categoryPath ? `/posts?categoryPath=${categoryPath}` : `/posts`;
    
    //글 목록 페이지 렌더링 하면 배너 숨기기
    setShowBanner(false);

    // 글 목록 가져오기
    api.get(url)
      .then(res => {
        setPosts(res.data.posts || []);
        setCategoryName(res.data.categoryName || ""); // API에서 카테고리 이름도 함께 반환
      })
      .catch(err => console.error(err));
      
    //페이지 떠날 때 배너 다시 표시하기 
    return ()=> setShowBanner(true);
  }, [categoryPath,setShowBanner]);

  return (
    <div style={{ padding: "20px" }}>
      {/* 배너는 여기서 아예 렌더링 안하면 숨겨진 상태 */}
      
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
          {posts?.length === 0 ? 
          (
            <tr><td colSpan={6} style={{ textAlign: "center", padding: "10px" }}>글이 없습니다.</td></tr>
          ) :
           (
            posts.map(post => (
              <tr key={post.id}>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>{post.id}</td>
                <td style={{ padding: "8px", borderBottom: "1px solid #eee" }}>
                  <Link to={`/${categoryPath}/posts/${post.id}`} style={{ color: "#333", textDecoration: "none" }}>
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
    </div>
  );
}
