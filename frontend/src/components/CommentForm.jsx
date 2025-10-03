import React, { useState } from "react";
import api from "../api/api";

function CommentForm({ categoryPath, postId, parentId = null, onCommentAdded }) {
  const [content, setContent] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) return;

    try {
      setLoading(true);
      const res = await api.post(`/${categoryPath}/posts/${postId}/comments`, {
        content,
        parentId,
      });
      onCommentAdded(res.data, parentId);
      setContent("");
    } catch (err) {
      console.error("댓글 작성 실패", err);
      alert("댓글 작성 실패");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginTop: 6 }}>
      <textarea
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder={parentId ? "답글을 입력하세요" : "댓글을 입력하세요"}
        style={{
          width: "100%",
          minHeight: "50px",
          padding: "6px",
          borderRadius: 6,
          border: "1px solid #ddd",
          resize: "vertical",
        }}
      />
      <button
        type="submit"
        disabled={loading}
        style={{
          marginTop: 4,
          padding: "4px 12px",
          borderRadius: 6,
          border: "none",
          backgroundColor: "#0095f6",
          color: "#fff",
          cursor: "pointer",
        }}
      >
        {loading ? "작성 중..." : parentId ? "답글 작성" : "댓글 작성"}
      </button>
    </form>
  );
}

export default CommentForm;
