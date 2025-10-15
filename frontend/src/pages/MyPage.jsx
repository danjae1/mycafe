import React, { useEffect, useState } from "react";
import api from "../api/api";
import jwt_decode from "jwt-decode";

export default function MyPage() {
  const [userInfo, setUserInfo] = useState(null);
  const [formData, setFormData] = useState({ username: "", email: "" });

  // JWT에서 사용자 ID
  const token = localStorage.getItem("accessToken");
  let userId = null;
  if (token) {
    const decoded = jwt_decode(token);
    userId = decoded.id;
  }

  // 회원정보 불러오기
  useEffect(() => { 
    const fetchUser = async () => {
      try {
        const res = await api.get(`/users/${userId}`);
        setUserInfo(res.data);
        setFormData({ username: res.data.username, email: res.data.email });
      } catch (err) {
        console.error(err);
      }
    };
    if (userId) fetchUser();
  }, [userId]);

  // 폼 변경 처리
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // 수정 저장
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.put(`/users/${userId}`, formData);
      alert("회원정보가 수정되었습니다.");
    } catch (err) {
      console.error(err);
      alert("수정 실패");
    }
  };

  if (!userInfo) return <p>로딩중...</p>;

  return (
    <div style={{ padding: "20px" }}>
      <h2>회원정보 수정</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>이름</label>
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
          />
        </div>
        <div>
          <label>이메일</label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
          />
        </div>
        <button type="submit">저장</button>
      </form>
    </div>
  );
}
