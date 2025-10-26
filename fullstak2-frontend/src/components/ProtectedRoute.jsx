import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  
  if (loading) return null;
  
  // Debe estar logueado
  if (!user) return <Navigate to="/login" replace />;
  
  // Solo el admin puede acceder al panel administrativo
  if (user.rol !== "ADMIN") {
    return (
      <div style={{ padding: "40px", textAlign: "center" }}>
        <h2>⛔ Acceso Denegado</h2>
        <p>Solo el administrador puede acceder a esta sección.</p>
        <a href="/">Volver al inicio</a>
      </div>
    );
  }
  
  return children;
}
