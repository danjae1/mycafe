import React, { useEffect, useState } from "react";
import CommentForm from "./CommentForm";
import api from "../api/api";
import jwt_decode from "jwt-decode";

function CommentItem({ comment, postId, categoryPath, onCommentAdded, level = 0 }) {
  if (!comment) return null; // comment 자체가 null이면 렌더링 안 함

  const [isEditing, setIsEditing] = useState(false); // 🔹 수정 모드 여부
  const [editedContent, setEditedContent] = useState(comment.content); // 🔹 수정 중 내용
  const [currentUserId, setCurrentUserId] = useState(null);
  const [showReplyForm, setShowReplyForm] = useState(false); // 답글 폼 표시 여부
  const [liked, setLiked] = useState(false); // 내가 좋아요 눌렀는지 여부
  const [likeCount, setLikeCount] = useState(0); // 좋아요 총 개수
  const indent = level * 20;

  const token = localStorage.getItem("accessToken");

  useEffect(() => {
    if (!token) return;

    const decoded = jwt_decode(token);
    setCurrentUserId(decoded.userId);

    // 내가 이 댓글에 좋아요 눌렀는지
    api
      .get(`/comments/${comment.id}/likes/me`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setLiked(res.data))
      .catch(() => setLiked(false));

    // 좋아요 개수
    api
      .post(`/comments/likes/counts`, [comment.id], {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setLikeCount(res.data[comment.id] || 0))
      .catch(() => setLikeCount(0));
  }, [comment.id, token]);

  const handleToggleLike = async () => {
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      await api.post(`/comments/${comment.id}/like`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setLiked((prev) => !prev);
      setLikeCount((prev) => (liked ? prev - 1 : prev + 1));
    } catch (err) {
      console.error("좋아요 토글 실패:", err);
      alert("좋아요 처리 중 오류가 발생했습니다.");
    }
  };

  const handleDelete = async () => {
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

    try {
      await api.delete(`/comments/${comment.id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("댓글이 삭제되었습니다.");
      onCommentAdded(null); // 삭제 후 새로고침 트리거
    } catch (err) {
      console.error(err);
      alert("댓글 삭제 실패");
    }
  };
  // 🔹 수정 저장
  const handleSaveEdit = async () => {
    if (editedContent.trim() === "") {
      alert("내용을 입력하세요.");
      return;
    }
    try {
      await api.patch(
        `/comments/${comment.id}`,
        { content: editedContent },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      alert("댓글이 수정되었습니다.");
      setIsEditing(false);
      onCommentAdded(null); // 목록 새로고침
    } catch (err) {
      console.error(err);
      alert("댓글 수정 실패");
    }
  };
  return (
    <li style={{ marginBottom: 12, position: "relative" }}>
      {/* ┃ 대댓글 구분용 세로선 */}
      {level > 0 && (
        <div
          style={{
            position: "absolute",
            left: indent - 10,
            top: 0,
            bottom: 0,
            width: 2,
            backgroundColor: "#ccc",
          }}
        />
      )}

      {/* 🔹 댓글 본문 박스 */}
      <div
        style={{
          marginLeft: indent,
          backgroundColor: "#f9f9f9",
          padding: "8px 12px",
          borderRadius: 8,
          border: "1px solid #eee",
          position: "relative",
        }}
      >
        {/* 답글 버튼 */}
        <button
          onClick={() => setShowReplyForm(!showReplyForm)}
          style={{
            position: "absolute",
            right: 8,
            top: 8,
            fontSize: "0.75rem",
            color: "#0095f6",
            backgroundColor: "#f0f0f0",
            border: "1px solid #ccc",
            borderRadius: 4,
            padding: "2px 6px",
            cursor: "pointer",
          }}
        >
          {showReplyForm ? "취소" : "답글 달기"}
        </button>

        {/* 삭제 버튼 */}
        {comment.userId === currentUserId && !comment.deleted && (
          <>
          <button
            onClick={handleDelete}
            style={{
              position: "absolute",
              right: 70,
              top: 8,
              fontSize: "0.75rem",
              color: "#f00",
              backgroundColor: "#fef0f0",
              border: "1px solid #fdd",
              borderRadius: 4,
              padding: "2px 6px",
              cursor: "pointer",
            }}
          >
            삭제
          </button>

          {/* 🔹 수정 버튼 */}
            <button
              onClick={() => setIsEditing(!isEditing)}
              style={{
                position: "absolute",
                right: 130,
                top: 8,
                fontSize: "0.75rem",
                color: "#555",
                backgroundColor: "#f0f0f0",
                border: "1px solid #ccc",
                borderRadius: 4,
                padding: "2px 6px",
                cursor: "pointer",
              }}
            >
              {isEditing ? "취소" : "수정"}
            </button>
            </>
        )}

        {/* 🔹 작성자 / 내용 / 작성일 */}
        <div style={{ fontWeight: "bold", marginBottom: 4 }}>
          {comment.userName || "익명"}
        </div>

        {isEditing ? (
          <>
            <textarea
              value={editedContent}
              onChange={(e) => setEditedContent(e.target.value)}
              rows={3}
              style={{
                width: "100%",
                resize: "none",
                padding: 6,
                borderRadius: 6,
                border: "1px solid #ccc",
              }}
            />
            <div style={{ marginTop: 6 }}>
              <button
                onClick={handleSaveEdit}
                style={{
                  marginRight: 8,
                  backgroundColor: "#0095f6",
                  color: "white",
                  border: "none",
                  borderRadius: 4,
                  padding: "4px 8px",
                }}
              >
                저장
              </button>
              <button
                onClick={() => setIsEditing(false)}
                style={{
                  backgroundColor: "#eee",
                  border: "1px solid #ccc",
                  borderRadius: 4,
                  padding: "4px 8px",
                }}
              >
                취소
              </button>
            </div>
          </>
        ) : (
          <div style={{ color: "#333", marginBottom: 6 }}>
            {comment.deleted ? "삭제된 댓글입니다." : comment.content}
          </div>
        )}

        <div style={{ fontSize: "0.75rem", color: "#888", marginBottom: 8 }}>
          작성일: {comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ""}
        </div>

        {/* ❤️ 좋아요 버튼 */}
        <button
          onClick={handleToggleLike}
          style={{
            background: "none",
            border: "none",
            cursor: "pointer",
            fontSize: "0.9rem",
            color: liked ? "red" : "#777",
            fontWeight: liked ? "bold" : "normal",
          }}
        >
          ♥ {likeCount}
        </button>

        {/* 🔹 답글 작성 폼 */}
        {showReplyForm && (
          <CommentForm
            categoryPath={categoryPath}
            postId={postId}
            parentId={comment.id}
            onCommentAdded={onCommentAdded}
          />
        )}
      </div>

      {/* 🔹 자식 댓글 재귀 렌더링 */}
      {comment.children?.length > 0 && (
        <ul style={{ listStyle: "none", paddingLeft: 0, marginTop: 8 }}>
          {comment.children.map((child) =>
            child ? (
              <CommentItem
                key={child.id}
                comment={child}
                postId={postId}
                categoryPath={categoryPath}
                onCommentAdded={onCommentAdded}
                level={level + 1}
              />
            ) : null
          )}
        </ul>
      )}
    </li>
  );
}

export default CommentItem;
