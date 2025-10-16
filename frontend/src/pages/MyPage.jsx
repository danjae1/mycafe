import React, { useEffect, useState } from "react";
import api from "../api/api";
import jwt_decode from "jwt-decode";

export default function MyPage() {
  const [userInfo, setUserInfo] = useState(null);
  const [formData, setFormData] = useState({ username: "", email: "" });
  const [showEditForm, setShowEditForm] = useState(false);
  const [showPasswordForm, setShowPasswordForm] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const token = localStorage.getItem("accessToken");

  // ✅ 유저 정보 가져오기
  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await api.get(`/users`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserInfo(res.data);
        setFormData({ username: res.data.username, email: res.data.email });
      } catch (err) {
        console.error("유저 불러오기 실패:", err);
      }
    };
    if (token) fetchUser();
  }, [token]);

  // ✅ 회원정보 수정
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.patch(`/users`, formData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("회원정보가 수정되었습니다.");
      setShowEditForm(false);
    } catch (err) {
      console.error("수정 실패:", err);
      alert("수정 실패");
    }
  };

  // ✅ 비밀번호 변경
  const handlePasswordChange = async (e) => {
    e.preventDefault();

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      alert("새 비밀번호가 일치하지 않습니다.");
      return;
    }

    try {
      await api.patch(`/password`, passwordData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("비밀번호가 변경되었습니다.");
      setPasswordData({ currentPassword: "", newPassword: "", confirmPassword: "" });
      setShowPasswordForm(false);
    } catch (err) {
      console.error("비밀번호 변경 실패:", err);
      alert("비밀번호 변경 실패");
    }
  };

  if (!userInfo) return <p>로딩중...</p>;

  return (
    <div style={{ padding: "20px" }}>
      <h2>마이페이지</h2>

      {/* 기본 정보 보기 */}
      {!showEditForm && !showPasswordForm && (
        <div>
          <p><strong>아이디:</strong> {userInfo.username}</p>
          <p><strong>이메일:</strong> {userInfo.email}</p>
          <p><strong>등급:</strong> {userInfo.grade}</p>
          <p>
            <strong>비밀번호:</strong> ******{" "}
            <button onClick={() => setShowPasswordForm(true)}>비밀번호 변경하기</button>
          </p>

          <button onClick={() => setShowEditForm(true)}>회원정보 수정하기</button>
        </div>
      )}

      {/* 회원정보 수정 폼 */}
      {showEditForm && (
        <form onSubmit={handleSubmit}>
          <h3>회원정보 수정</h3>
          <div>
            <label>이름</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
            />
          </div>
          <div>
            <label>이메일</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            />
          </div>
          <button type="submit">저장</button>
          <button type="button" onClick={() => setShowEditForm(false)}>취소</button>
        </form>
      )}

      {/* 비밀번호 변경 폼 */}
      {showPasswordForm && (
        <form onSubmit={handlePasswordChange}>
          <h3>비밀번호 변경</h3>
          <div>
            <label>현재 비밀번호</label>
            <input
              type="password"
              name="currentPassword"
              value={passwordData.currentPassword}
              onChange={(e) =>
                setPasswordData({ ...passwordData, currentPassword: e.target.value })
              }
            />
          </div>
          <div>
            <label>새 비밀번호</label>
            <input
              type="password"
              name="newPassword"
              value={passwordData.newPassword}
              onChange={(e) =>
                setPasswordData({ ...passwordData, newPassword: e.target.value })
              }
            />
          </div>
          <div>
            <label>새 비밀번호 확인</label>
            <input
              type="password"
              name="confirmPassword"
              value={passwordData.confirmPassword}
              onChange={(e) =>
                setPasswordData({ ...passwordData, confirmPassword: e.target.value })
              }
            />
          </div>
          <button type="submit">비밀번호 변경</button>
          <button type="button" onClick={() => setShowPasswordForm(false)}>취소</button>
        </form>
      )}
    </div>
  );
}
