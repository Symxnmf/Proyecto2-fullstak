import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { loginApi, registerApi } from "../api/apiAuth";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const saved = localStorage.getItem("auth");
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        setUser(parsed.user || null);
        setToken(parsed.token || null);
        setRefreshToken(parsed.refreshToken || null);
      } catch (_) {}
    }
    setLoading(false);
  }, []);

  const saveSession = (data) => {
    const payload = { user: data.user || null, token: data.token || null, refreshToken: data.refreshToken || null };
    localStorage.setItem("auth", JSON.stringify(payload));
    setUser(payload.user);
    setToken(payload.token);
    setRefreshToken(payload.refreshToken);
  };

  const login = async (credenciales) => {
    try {
      // Primer intento: login contra API del backend
      const data = await loginApi(credenciales);
      if (data && (data.token || data.user)) {
        saveSession(data);
        return data;
      }
    } catch (_) {
      // Si falla la API, intentar fallbacks locales
    }
    
    // Fallback 1: Validar admin hardcodeado
    const adminEmail = "Admin@duocuc.cl";
    // Permitir variantes de la contraseña por compatibilidad
    const adminPassVariants = ["Duoc12345.", "Duoc12345"];
    if (
      (credenciales?.email || "").toLowerCase() === adminEmail.toLowerCase() &&
      adminPassVariants.includes(String(credenciales?.password))
    ) {
      // Admin local para trabajar offline
      const localAdmin = { user: { email: adminEmail, nombre: "Admin", rol: "ADMIN" }, token: "local-admin" };
      saveSession(localAdmin);
      return localAdmin;
    }
    
    // Fallback 2: Buscar en usuarios guardados localmente
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
      return { ok: true, mensaje: "Registro exitoso. Inicia sesión con tus credenciales." };
    } catch (_) {
      const key = "auth_users";
      const raw = localStorage.getItem(key);
      const list = raw ? JSON.parse(raw) : [];
      const exists = list.some(u => (u.email || "").toLowerCase() === (info.email || "").toLowerCase());
      if (exists) throw new Error("El email ya está registrado");
      
      const nuevo = { email: info.email, nombre: info.nombre || "Usuario", password: String(info.password || "") };
      list.push(nuevo);
      localStorage.setItem(key, JSON.stringify(list));
      return { ok: true, mensaje: "Registro local exitoso. Ahora inicia sesión." };
    }
  };

  const logout = () => {
    localStorage.removeItem("auth");
    setUser(null);
    setToken(null);
    setRefreshToken(null);
  };

  const value = useMemo(() => ({ user, token, refreshToken, loading, login, register, logout, saveSession }), [user, token, refreshToken, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * Hook para consumir el contexto de autenticación
 * @throws {Error} si se usa fuera del AuthProvider
 * @returns {Object} { user, token, loading, login, register, logout }
 */
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth debe usarse dentro de AuthProvider");
  return ctx;
}
