import React, { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../api/api";
import { Editor } from "@toast-ui/react-editor";
import "@toast-ui/editor/dist/toastui-editor.css";

function PostEdit() {
  const { categoryPath, postId } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef();

  const [title, setTitle] = useState("");
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");

  // ✅ 게시글 불러오기
  useEffect(() => {
    api.get(`/${categoryPath}/posts/${postId}`)
      .then(res => {
        setTitle(res.data.title);
        editorRef.current.getInstance().setHTML(res.data.content);
        setSelectedCategory(res.data.categoryId); // 기존 카테고리 선택
      })
      .catch(err => console.error("게시글 불러오기 실패:", err));
  }, [categoryPath, postId]);

  // ✅ 카테고리 목록 불러오기
  useEffect(() => {
    api.get("/categories")
      .then(res => setCategories(res.data.categories))
      .catch(err => console.error("카테고리 불러오기 실패:", err));
  }, []);

  // ✅ 글 수정 제출
  const handleSubmit = async (e) => {
    e.preventDefault();
    const content = editorRef.current.getInstance().getHTML();

    try {
      await api.patch(`/${postId}`, {
        title,
        content,
        categoryId: selectedCategory,
      });
      navigate(`/${categoryPath}/posts/${postId}`);
    } catch (err) {
      console.error(err);
      alert("수정 중 오류가 발생했습니다.");
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ maxWidth: 900, margin: "0 auto" }}>
      {/* 카테고리 선택 */}
      <div style={{ marginBottom: 12 }}>
        <label style={{ marginRight: 8 }}>카테고리:</label>
        <select
          value={selectedCategory}
          onChange={(e) => setSelectedCategory(e.target.value)}
          style={{ padding: 8 }}
        >
          <option value="">-- 카테고리 선택 --</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>

      {/* 제목 */}
      <input
        value={title}
        onChange={e => setTitle(e.target.value)}
        style={{ width: "100%", padding: 10, marginBottom: 12 }}
      />

      {/* 내용 */}
      <Editor
        ref={editorRef}
        placeholder="내용 입력..."
        previewStyle="vertical"
        height="500px"
        initialEditType="wysiwyg"
      />

      <button type="submit" style={{ marginTop: 16 }}>수정 완료</button>
    </form>
  );
}

export default PostEdit;
