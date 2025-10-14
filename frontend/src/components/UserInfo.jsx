import React, { useEffect, useState } from "react";
import api from "../api/api";

export default function UserInfo() {

  const [userSummary,setUserSummary] = useState(null);
  const [loading, setLoading] = useState(true);

    useEffect(()=>{
      api.get("/users/me")
      .then((res)=>{
        setUserSummary(res.data);
      })
      .catch((err)=>{
        console.log("유저 요약 정보 가져오기 실패 : ", err);
      })
      .finally(()=> setLoading(false));
    },[]);
    
  
  if (loading) return <p>로딩 중...</p>;
  if (!userSummary) return <p>정보를 불러올 수 없습니다.</p>;

  return ( 
    <div style={{ textAlign: "center" }}>
      <p><strong>{userSummary.nickname}</strong></p>
      <p>등급: {userSummary.grade}</p>
      <p>내가 쓴 글: {userSummary.postCount}개</p>
      <p>내가 쓴 댓글: {userSummary.commentCount}개</p>
      <p>가입일: {new Date(userSummary.joinDate).toLocaleDateString()}</p>
    </div>
  );
}
