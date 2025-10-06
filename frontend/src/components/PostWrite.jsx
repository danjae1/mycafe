import React, { useEffect, useRef, useState } from "react";
import { Editor } from "@toast-ui/react-editor";
import "@toast-ui/editor/dist/toastui-editor.css";
import api from "../api/api";
import Prism from "prismjs";
import codeSyntaxHighlight from "@toast-ui/editor-plugin-code-syntax-highlight";
import "@toast-ui/editor-plugin-code-syntax-highlight/dist/toastui-editor-plugin-code-syntax-highlight.css";
import { useNavigate } from "react-router-dom";

function PostWrite({ setShowBanner }) {
  const editorRef = useRef();
  const navigate = useNavigate();

  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");

  // ✅ 카테고리 목록 불러오기
  useEffect(() => {
    setShowBanner?.(false);

    api
      .get("/categories")
      .then((res) => {
        console.log(res.data)
        setCategories(res.data.categories);
      })
      .catch((err) => console.error("카테고리 불러오기 실패:", err));

    return () => setShowBanner?.(true);
  }, [setShowBanner]);

  // ✅ 글 등록
  const handleSubmit = async (e) => {
    e.preventDefault();

    const title = e.target.title.value;
    const editorInstance = editorRef.current.getInstance();
    const content = editorInstance.getHTML();

    if (!selectedCategory) {
      alert("카테고리를 선택해주세요!");
      return;
    }

    try {
      const res = await api.post("/posts", {
        title,
        content,
        categoryId: selectedCategory, 
      });

      alert("글이 등록되었습니다!");
      const postId = res.data.id;
      const categoryPath = res.data.categoryPath; // PostResponseDto에 categoryPath 있어야 함

      navigate(`/${categoryPath}/posts/${postId}`);
    } catch (err) {
      console.error(err);
      alert("등록 중 오류가 발생했습니다.");
    }
  };

  return (
    <div style={{ maxWidth: "900px", margin: "0 auto" }}>
      <h2 style={{ marginBottom: "16px" }}>✏️ 글 작성</h2>

      <form onSubmit={handleSubmit}>
        {/* ✅ 카테고리 선택 */}
        <div style={{ marginBottom: "12px" }}>
          <label style={{ marginRight: "8px" }}>카테고리:</label>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            style={{ padding: "8px" }}
            required
          >
            <option value="">-- 카테고리 선택 --</option>
            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>
        </div>

        <input
          type="text"
          name="title"
          placeholder="제목을 입력하세요"
          style={{
            width: "100%",
            padding: "10px",
            fontSize: "16px",
            marginBottom: "12px",
          }}
          required
        />

        <Editor
          ref={editorRef}
          placeholder="내용을 입력하세요..."
          previewStyle="vertical"
          height="500px"
          initialEditType="wysiwyg"
          useCommandShortcut={true}
          plugins={[[codeSyntaxHighlight, { highlighter: Prism }]]}
        />

        <button
          type="submit"
          style={{
            marginTop: "16px",
            padding: "10px 20px",
            backgroundColor: "#333",
            color: "#fff",
            border: "none",
            borderRadius: "6px",
            cursor: "pointer",
          }}
        >
          등록하기
        </button>
      </form>
    </div>
  );
}

export default PostWrite;
