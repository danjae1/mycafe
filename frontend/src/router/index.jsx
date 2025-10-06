import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import PostList from "../components/PostList";
import PostDetail from "../components/PostDetail";
import PostWrite from "../components/PostWrite";
import PostEdit from "../components/PostEdit";


export default function AppRouter({setShowBanner}) {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      
      {/* 카테고리별 글 목록 동적라우팅 + showBanner값 전달해서 메인배너 올리기와 내리기 */}
      <Route path="/:categoryPath" element={<PostList setShowBanner={setShowBanner} />} />

      <Route path="/:categoryPath/posts/:postId" element={<PostDetail setShowBanner={setShowBanner} />} />
     <Route path="/write" element={<PostWrite />} />
     <Route path="/:categoryPath/posts/:postId/edit" element={<PostEdit />} />
    </Routes>
  );
}
