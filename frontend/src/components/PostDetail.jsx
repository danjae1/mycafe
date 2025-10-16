import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import jwt_decode from "jwt-decode";
import api from "../api/api";
import CommentForm from "./CommentForm";
import CommentList from "./CommentList";

export default function PostDetail({ setShowBanner }) {
  const { categoryPath, postId } = useParams();
  const navigate = useNavigate();

  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);

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
    
    if (token) {
      api.get(`/${postId}/like`, {
        headers: { Authorization: `Bearer ${token}` },
      })  
        .then((res) => {
          setLiked(res.data); // Boolean 반환한다고 가정
        })
        .catch((err) => console.error("좋아요 상태 불러오기 실패:", err));
    }
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

  
  const handleToggleLike = async () => {
  if (!token) {
    alert("로그인이 필요합니다.");
    return;
  }
  try {
    const res = await api.post(
      `/${postId}/like`,
      {},
      { headers: { Authorization: `Bearer ${token}` } }
    );
    setLiked((prev) => !prev);
    setLikeCount(res.data.likeCount); // 백엔드에서 갱신된 수 내려줄 경우
  } catch (err) {
    console.error(err);
    alert("좋아요 처리 실패");
  }
};

  console.log("currentUser", currentUser); 
  console.log("post.userId", post.userId);

  return (
    <div style={{
              transition: "color 0.2s ease", // 부드러운 색 전환
          }}
    >
      <button onClick={() => navigate(-1)} style={{ marginBottom: 12 }}>◀ 뒤로</button>

      <h2>{post.title}</h2>
      <div style={{ color: "#666", marginBottom: 10 }}>
        작성자 :   
        <span className = "username" onClick={()=> navigate(`/members/${post.userId}?tab=articles`)}
          style = {{cursor: "pointer"}}>           
           {post.writer}
        </span>
        {/* postDetail LocalDate타입 확인 */}
        {/* 작성일: {post.createdAt ? new Date(post.createdAt).toLocaleString() : ""} */}
        작성일: {post.createdAt}
      </div>
      <div style={{ marginBottom: 10, color: "#888", display: "flex", alignItems: "center", gap: "8px" }}>
        <span>조회수: {post.viewCount ?? 0}</span>
        <span>추천: {likeCount}</span>
        <button
          onClick={handleToggleLike}
          style={{
            border: "none",
            background: "none",
            color: liked ? "red" : "#555",
            cursor: "pointer",
            fontSize: "1rem",
            fontWeight: liked ? "bold" : "normal",
          }}
        >
          ♥
        </button>
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
