import React from "react";

export default function Pagination({
  currentPage,
  totalPages,
  startPageNum,
  endPageNum,
  onPageChange,
}) {
  const handlePrevGroup = () => {
    if (startPageNum > 1) onPageChange(startPageNum - 1);
  };

  const handleNextGroup = () => {
    if (endPageNum < totalPages) onPageChange(endPageNum + 1);
  };

  return (
    <div style={{ marginTop: "20px", textAlign: "center" }}>
      <button
        onClick={handlePrevGroup}
        disabled={startPageNum === 1}
        style={{
          margin: "0 5px",
          padding: "4px 8px",
          cursor: startPageNum === 1 ? "default" : "pointer",
        }}
      >
        &lt;
      </button>

      {Array.from(
        { length: endPageNum - startPageNum + 1 },
        (_, i) => startPageNum + i
      ).map((page) => (
        <button
          key={page}
          onClick={() => onPageChange(page)}
          style={{
            margin: "0 4px",
            fontWeight: currentPage === page ? "bold" : "normal",
            backgroundColor: currentPage === page ? "#e0e0e0" : "white",
            border: "1px solid #ccc",
            borderRadius: "4px",
            padding: "4px 8px",
            cursor: "pointer",
          }}
        >
          {page}
        </button>
      ))}

      <button
        onClick={handleNextGroup}
        disabled={endPageNum >= totalPages}
        style={{
          margin: "0 5px",
          padding: "4px 8px",
          cursor: endPageNum >= totalPages ? "default" : "pointer",
        }}
      > 
        &gt;
      </button>
    </div>
  );
}
