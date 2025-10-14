import React, { useEffect, useState } from "react";
import api from "../api/api";

export default function CafeInfo() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/cafe/summary")
      .then((res) => setSummary(res.data))
      .catch((err) => console.error("카페 통계 가져오기 실패:", err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>로딩 중...</p>;
  if (!summary) return <p>정보를 불러올 수 없습니다.</p>;

  return (
    <div style={{ textAlign: "center" }}>
      <p> 임채호 카페</p>
      <p>회원 수: {summary.userCount}명</p>
      <p>전체 글 수: {summary.postCount}개</p>
      <p>전체 댓글 수: {summary.commentCount}개</p>
    </div>
  );
}
