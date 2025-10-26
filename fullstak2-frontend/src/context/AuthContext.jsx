import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { loginApi, registerApi } from "../api/apiAuth";

/**
 * Context para manejo de autenticación global
 * Provee estado de usuario, token y funciones de login/logout
 */
const AuthContext = createContext(null);

/**
 * Provider de autenticación
 * Maneja persistencia en localStorage y fallbacks offline
 */
export function AuthProvider({ children }) {
  // Estado del usuario actual
  const [user, setUser] = useState(null);
  // Token de autenticación
  const [token, setToken] = useState(null);
  // Flag de carga inicial
  const [loading, setLoading] = useState(true);

  // Efecto para cargar sesión guardada al montar
  useEffect(() => {
    // Intentar recuperar sesión desde localStorage
    const saved = localStorage.getItem("auth");
    if (saved) {
      try {
        // Parsear y restaurar sesión
        const parsed = JSON.parse(saved);
        setUser(parsed.user || null);
        setToken(parsed.token || null);
      } catch (_) {
        // Si hay error, ignorar y continuar sin sesión
      }
    }
    setLoading(false);
  }, []);

  /**
   * Guarda la sesión en estado y localStorage
   * @param {Object} data - Objeto con user y token
   */
  const saveSession = (data) => {
    const payload = { user: data.user || null, token: data.token || null };
    // Guardar en localStorage para persistencia
    localStorage.setItem("auth", JSON.stringify(payload));
    // Actualizar estado de React
    setUser(payload.user);
    setToken(payload.token);
  };

  /**
   * Función de login
   * Intenta autenticar contra API, si falla usa fallbacks locales
   * @param {Object} credenciales - email y password
   * @returns {Object} datos del usuario y token
   */
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
    const key = "auth_users"; // Key para localStorage
    const raw = localStorage.getItem(key);
    const list = raw ? JSON.parse(raw) : [];
    // Buscar usuario por email (case insensitive)
    const found = list.find(u => (u.email || "").toLowerCase() === (credenciales.email || "").toLowerCase());
    if (found && found.password === String(credenciales.password)) {
      // Usuario encontrado localmente
      const localUser = { user: { email: found.email, nombre: found.nombre, rol: "CLIENTE" }, token: "local-user" };
      saveSession(localUser);
      return localUser;
    }
    
    // Si no se encontró, credenciales inválidas
    throw new Error("Credenciales inválidas");
  };

  /**
   * Función de registro
   * Registra nuevo usuario en API o localmente
   * @param {Object} info - nombre, email y password
   * @returns {Object} datos del usuario registrado
   */
  const register = async (info) => {
    try {
      // Intentar registro en API
      const data = await registerApi(info);
      if (!data?.token) {
        // Si el backend no devuelve token, guardar sesión local
        saveSession({ user: { email: info.email, nombre: info.nombre || "Usuario", rol: "CLIENTE" }, token: "local-user" });
      }
      return data;
    } catch (_) {
      // Si falla la API, guardar usuario localmente
      const key = "auth_users";
      const raw = localStorage.getItem(key);
      const list = raw ? JSON.parse(raw) : [];
      // Verificar que no exista ya el email
      const exists = list.some(u => (u.email || "").toLowerCase() === (info.email || "").toLowerCase());
      if (exists) throw new Error("El email ya está registrado");
      
      // Crear nuevo usuario local
      const nuevo = { email: info.email, nombre: info.nombre || "Usuario", password: String(info.password || "") };
      list.push(nuevo);
      localStorage.setItem(key, JSON.stringify(list));
      
      // Iniciar sesión automáticamente
      saveSession({ user: { email: nuevo.email, nombre: nuevo.nombre, rol: "CLIENTE" }, token: "local-user" });
      return { ok: true };
    }
  };

  /**
   * Cierra la sesión actual
   * Limpia estado y localStorage
   */
  const logout = () => {
    // Limpiar localStorage
    localStorage.removeItem("auth");
    // Resetear estado
    setUser(null);
    setToken(null);
  };

  // Memoizar el value del context para evitar re-renders innecesarios
  const value = useMemo(() => ({ user, token, loading, login, register, logout }), [user, token, loading]);

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
