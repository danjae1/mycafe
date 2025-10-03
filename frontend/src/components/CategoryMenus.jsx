import React from "react";
import { Nav } from "react-bootstrap";
import { NavLink } from "react-router-dom";

export default function CategoryMenus() {
  return (
    <Nav className="flex-column" style={{ marginLeft: "2cm" }}>
      <Nav.Item className="mb-2">
        <NavLink
          to="/"
          className={({ isActive }) =>
            isActive ? "nav-link fw-bold" : "nav-link"
          }
        >
          홈
        </NavLink>
      </Nav.Item>
      <Nav.Item className="mb-2">
        <NavLink
          to="/free"
          className={({ isActive }) =>
            isActive ? "nav-link fw-bold" : "nav-link"
          }
        >
          자유게시판
        </NavLink>
      </Nav.Item>
      <Nav.Item className="mb-2">
        <NavLink
          to="/etc"
          className={({ isActive }) =>
            isActive ? "nav-link fw-bold" : "nav-link"
          }
        >
          @@게시판
        </NavLink>
      </Nav.Item>
      <Nav.Item className="mb-2">
        <NavLink
          to="/popular"
          className={({ isActive }) =>
            isActive ? "nav-link fw-bold" : "nav-link"
          }
        >
          인기게시판
        </NavLink>
      </Nav.Item>
    </Nav>
  );
}
