import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { loginApi, registerApi } from "../api/apiAuth";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // cargar desde localStorage
    const saved = localStorage.getItem("auth");
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        setUser(parsed.user || null);
        setToken(parsed.token || null);
      } catch (_) {}
    }
    setLoading(false);
  }, []);

  const saveSession = (data) => {
    const payload = { user: data.user || null, token: data.token || null };
    localStorage.setItem("auth", JSON.stringify(payload));
    setUser(payload.user);
    setToken(payload.token);
  };

  const login = async (credenciales) => {
    try {
      const data = await loginApi(credenciales);
      if (data && (data.token || data.user)) {
        saveSession(data);
        return data;
      }
    } catch (_) {
      // seguimos a fallbacks locales
    }
    // Fallback: Admin local
    const adminEmail = "Admin@duocuc.cl";
    const adminPassVariants = ["Duoc12345.", "Duoc12345"]; // admitir ambas mientras tanto
    if (
      (credenciales?.email || "").toLowerCase() === adminEmail.toLowerCase() &&
      adminPassVariants.includes(String(credenciales?.password))
    ) {
      const localAdmin = { user: { email: adminEmail, nombre: "Admin", rol: "ADMIN" }, token: "local-admin" };
      saveSession(localAdmin);
      return localAdmin;
    }
    // Fallback: usuarios guardados localmente
    const key = "auth_users";
    const raw = localStorage.getItem(key);
    const list = raw ? JSON.parse(raw) : [];
    const found = list.find(u => (u.email || "").toLowerCase() === (credenciales.email || "").toLowerCase());
    if (found && found.password === String(credenciales.password)) {
      const localUser = { user: { email: found.email, nombre: found.nombre, rol: "CLIENTE" }, token: "local-user" };
      saveSession(localUser);
      return localUser;
    }
    throw new Error("Credenciales inválidas");
  };

  const register = async (info) => {
    try {
      const data = await registerApi(info);
      if (!data?.token) {
        saveSession({ user: { email: info.email, nombre: info.nombre || "Usuario", rol: "CLIENTE" }, token: "local-user" });
      }
      return data;
    } catch (_) {
      // Guardar en localStorage como fallback
      const key = "auth_users";
      const raw = localStorage.getItem(key);
      const list = raw ? JSON.parse(raw) : [];
      const exists = list.some(u => (u.email || "").toLowerCase() === (info.email || "").toLowerCase());
      if (exists) throw new Error("El email ya está registrado");
      const nuevo = { email: info.email, nombre: info.nombre || "Usuario", password: String(info.password || "") };
      list.push(nuevo);
      localStorage.setItem(key, JSON.stringify(list));
      saveSession({ user: { email: nuevo.email, nombre: nuevo.nombre, rol: "CLIENTE" }, token: "local-user" });
      return { ok: true };
    }
  };

  const logout = () => {
    localStorage.removeItem("auth");
    setUser(null);
    setToken(null);
  };

  const value = useMemo(() => ({ user, token, loading, login, register, logout }), [user, token, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de AuthProvider");
  return ctx;
}
