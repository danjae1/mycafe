import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";

export default function Sidebar() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    axios.get("/api/categories")
      .then(res => setCategories(res.data.categories)
    )
      .catch(err => console.error(err));
  }, []);

  return (
    <div
      style={{
        width: "82%",
        height: "calc(100vh - 13cm)",
        backgroundColor: "#f8f9fa",
        padding: "20px",
        borderRadius: "8px",
        boxShadow: "0 0 10px rgba(0,0,0,0.1)",
        display: "flex",
        flexDirection: "column",
        overflowY: "auto",
      }}
    >
      {categories.length === 0 ? (
        <p>카테고리가 없습니다.</p>
      ) : (
        categories.map(cat => (
          <Link
            key={cat.id}
            to={`${cat.path}`}
            style={{
              display: "block",
              padding: "10px 15px",
              marginBottom: "8px",
              borderRadius: "5px",
              textDecoration: "none",
              color: "#333",
              backgroundColor: "#e9ecef",
            }}
          >
            {cat.name}
          </Link>
        ))
      )}
    </div>
  );
}
