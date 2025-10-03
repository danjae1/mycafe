import React, { useState } from "react";
import CommentForm from "./CommentForm";
import api from "../api/api";

function CommentItem({ comment, postId, categoryPath, onCommentAdded, level = 0 }) {
  const [showReplyForm, setShowReplyForm] = useState(false);
  const indent = level * 20;

  const handleDelete = async () => {
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

    try {
      await api.delete(`/${categoryPath}/posts/${postId}/comments/${comment.id}`);
      alert("댓글이 삭제되었습니다.");
      onCommentAdded(null); // 삭제 후 새로고침
    } catch (err) {
      console.error(err);
      alert("댓글 삭제 실패");
    }
  };

  return (
    <li style={{ marginBottom: 12, position: "relative" }}>
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

        <div style={{ fontWeight: "bold", marginBottom: 4 }}>{comment.userName || "익명"}</div>
        <div style={{ color: "#333", marginBottom: 4 }}>{comment.content}</div>
        <div style={{ fontSize: "0.75rem", color: "#888" }}>
          작성일: {comment.createdAt ? new Date(comment.createdAt).toLocaleString() : ""}
        </div>

        {showReplyForm && (
          <CommentForm
            categoryPath={categoryPath}
            postId={postId}
            parentId={comment.id}
            onCommentAdded={onCommentAdded}
          />
        )}
      </div>

      {comment.children?.length > 0 && (
        <ul style={{ listStyle: "none", paddingLeft: 0, marginTop: 8 }}>
          {comment.children.map((child) => (
            <CommentItem
              key={child.id}
              comment={child}
              postId={postId}
              categoryPath={categoryPath}
              onCommentAdded={onCommentAdded}
              level={level + 1}
            />
          ))}
        </ul>
      )}
    </li>
  );
}

export default CommentItem;
