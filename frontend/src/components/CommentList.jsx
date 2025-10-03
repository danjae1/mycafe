import React from "react";
import CommentItem from "./CommentItem";

function CommentList({ comments, postId, categoryPath, onCommentAdded }) {
  if (!comments || comments.length === 0) {
    return <div style={{ color: "#666" }}>댓글이 없습니다.</div>;
  }

  return (
    <ul style={{ listStyle: "none", paddingLeft: 0 }}>
      {comments.map(comment => (
        <CommentItem
          key={comment.id}
          comment={comment}
          postId={postId}
          categoryPath={categoryPath}
          onCommentAdded={onCommentAdded}
        />
      ))}
    </ul>
  );
}

export default CommentList;
