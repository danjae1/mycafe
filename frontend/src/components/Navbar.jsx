import { Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

export default function Navbar({ onLoginClick, onSignupClick ,isLoggedIn,onLogout}) {
  
  const navigate = useNavigate();

  return (
    <div style={{
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      backgroundColor: "#f8f9fa",
      borderBottom: "1px solid #ddd",
      boxSizing: "border-box",
    }}>
      <Button
        variant="outline-dark"
        size="sm"
        style={{ fontWeight: "bold", color: "green", fontSize: "0.8rem" }}
        onClick={() => navigate("/")}
      >
        MY CAFE
      </Button>

      <div style={{ display: "flex", alignItems: "center", gap: "1.5rem" }}>
        {isLoggedIn ? (
          <>
            <Button
              variant="outline-dark"
              size="sm"
              style={{ color: "black", fontSize: "0.8rem" }}
              onClick={() => navigate("/")}
            >
              마이페이지
            </Button>
            <Button
              variant="outline-dark"
              size="sm"
              style={{ color: "black", fontSize: "0.8rem" }}
              onClick={onLogout}
            >
              로그아웃
            </Button>
          </>
        ) :(
        <>
        <Button
          variant="outline-dark"
          size="sm"
          style={{ color: "black", fontSize: "0.8rem" }}
          onClick={onLoginClick}   // 여기!!
        >
          로그인
        </Button>
        <Button
          variant="outline-dark"
          size="sm"
          style={{ color: "black", fontSize: "0.8rem" }}
          onClick={onSignupClick}  // 여기!!
        >
          회원가입
        </Button>
        </>
      )}
    
      </div>
    </div>
  );
}
