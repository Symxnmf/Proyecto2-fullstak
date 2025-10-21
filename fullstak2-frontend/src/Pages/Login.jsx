import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Login.css";

export default function Login() {
  const { login, register } = useAuth();
  const navigate = useNavigate();
  const [modo, setModo] = useState("login");
  const [form, setForm] = useState({ nombre: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setCargando(true);
    try {
      if (modo === "login") {
        await login({ email: form.email, password: form.password });
      } else {
        await register({ nombre: form.nombre, email: form.email, password: form.password });
      }
      navigate("/admin");
    } catch (err) {
      setError("No se pudo completar la operación. Verifica tus datos.");
      console.error(err);
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-toggle">
          <button className={`toggle-btn ${modo === "login" ? "active" : ""}`} onClick={() => setModo("login")}>Iniciar sesión</button>
          <button className={`toggle-btn ${modo === "register" ? "active" : ""}`} onClick={() => setModo("register")}>Crear cuenta</button>
        </div>
        <h1 className="auth-title">{modo === "login" ? "¡Bienvenido de nuevo!" : "Crea tu cuenta"}</h1>
        <p className="auth-subtitle">Pastelería Dulce Sabor</p>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={onSubmit} className="auth-form">
          {modo === "register" && (
            <div className="form-group">
              <label>Nombre</label>
              <input type="text" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required />
            </div>
          )}
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Contraseña</label>
            <input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
          </div>
          <button className="submit-btn" disabled={cargando}>{cargando ? "Procesando..." : (modo === "login" ? "Entrar" : "Crear cuenta")}</button>
        </form>
      </div>
    </div>
  );
}
