import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import jwt_decode from "jwt-decode";
import api from "../api/api";
import CommentForm from "./CommentForm";
import CommentList from "./CommentList";

export default function PostDetail({ setShowBanner }) {
  const { categoryPath, postId } = useParams();
  const navigate = useNavigate();

  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 토큰에서 로그인한 유저 정보 가져오기
  const token = localStorage.getItem("accessToken");
  console.log(token);
  let currentUser = null;
  if (token) {
    const decoded = jwt_decode(token);
    currentUser = { id: decoded.userId, username: decoded.username }; // payload 구조에 맞게 조정
    console.log(currentUser)
  }
  
  useEffect(() => {
    setShowBanner(false);
    setLoading(true);

    // 글 상세 API 호출
    api.get(`/${categoryPath}/posts/${postId}`)
      .then(res => 
        {setPost(res.data)
        console.log(res)
        console.log(res.data)})
      .catch(err => setError(err.response?.data?.error || "글 조회 실패"));

    // 댓글/대댓글 API 호출
    api.get(`/${categoryPath}/posts/${postId}/comments`)
      .then(res => setComments(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));

    return () => setShowBanner(true);
  }, [postId, token, setShowBanner]);

  if (loading) return <div style={{ padding: 20 }}>로딩중...</div>;
  if (error) return <div style={{ padding: 20, color: "red" }}>에러: {error}</div>;
  if (!post) return null;

  const handleCommentAdded = (newComment) => {
    setComments(prev => [...prev, newComment]);
  };

  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await api.delete(`/${postId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      alert("게시글이 삭제되었습니다.");
      navigate(-1);
    } catch (err) {
      console.error(err);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };
  console.log("currentUser", currentUser); 
  console.log("post.userId", post.userId);
  return (
    <div style={{ padding: 20 }}>
      <button onClick={() => navigate(-1)} style={{ marginBottom: 12 }}>◀ 뒤로</button>

      <h2>{post.title}</h2>
      <div style={{ color: "#666", marginBottom: 10 }}>
        작성자: {post.writer} &nbsp;|&nbsp;
        작성일: {post.createdAt ? new Date(post.createdAt).toLocaleString() : ""}
      </div>
      <div style={{ marginBottom: 10, color: "#888" }}>
        조회수: {post.viewCount ?? 0} &nbsp; 추천: {post.likeCount ?? 0}
      </div>

      {/* 작성자이면 수정/삭제 버튼 표시 */}
      {currentUser?.id === post.userId && (
        <div style={{ marginBottom: 12 }}>
          <button
            onClick={() => navigate(`/${categoryPath}/posts/${postId}/edit`)}
            style={{ marginRight: 8 }}
          >
            수정
          </button>
          <button onClick={handleDelete}>삭제</button>
        </div>
      )}

      <hr />

      <div style={{ marginTop: 12 }} dangerouslySetInnerHTML={{ __html: post.content || "" }} />

      <hr style={{ margin: "20px 0" }} />
      <h3>댓글</h3>
      <CommentForm 
        postId={postId} 
        categoryPath={categoryPath} 
        onCommentAdded={handleCommentAdded} 
      />

      <CommentList 
        comments={comments} 
        postId={postId}
        categoryPath={categoryPath}
        onCommentAdded={handleCommentAdded}
      />
    </div>
  );
}
