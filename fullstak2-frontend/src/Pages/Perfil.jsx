import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { obtenerMisPedidos } from "../api/apiPedidos";

export default function Perfil() {
  const { user } = useAuth();
  const [pedidos, setPedidos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let mounted = true;
    async function cargar() {
      setLoading(true);
      setError("");
      try {
        const data = await obtenerMisPedidos();
        if (mounted) setPedidos(Array.isArray(data) ? data : []);
      } catch (e) {
        if (mounted) setError("No se pudo cargar tu historial. Inicia sesión nuevamente.");
      } finally {
        if (mounted) setLoading(false);
      }
    }
    if (user) cargar(); else { setLoading(false); setPedidos([]); }
    return () => { mounted = false; };
  }, [user]);

  if (!user) {
    return (
      <div className="container py-4">
        <h2>Mi perfil</h2>
        <p>Debes iniciar sesión para ver tu historial de compras.</p>
        <a className="btn btn-primary" href="/login">Ir a iniciar sesión</a>
      </div>
    );
  }

  return (
    <div className="container py-4">
      <h2>Mi perfil</h2>
      <div className="mb-3">
        <strong>Usuario:</strong> {user?.nombre || user?.email}
      </div>

      <h4>Historial de compras</h4>
      {loading && <p>Cargando...</p>}
      {error && <div className="alert alert-danger">{error}</div>}
      {!loading && pedidos.length === 0 && !error && (
        <p className="text-muted">Aún no tienes compras registradas.</p>
      )}

      {pedidos.length > 0 && (
        <div className="table-responsive">
          <table className="table table-sm table-striped">
            <thead>
              <tr>
                <th>Boleta</th>
                <th>Fecha</th>
                <th>Subtotal</th>
                <th>IVA</th>
                <th>Total</th>
                <th>Items</th>
              </tr>
            </thead>
            <tbody>
              {pedidos.map(p => (
                <tr key={p.id}>
                  <td>{p.numero || p.id}</td>
                  <td>{p.fecha}</td>
                  <td>{Number(p.subtotal || 0).toLocaleString('es-CL')}</td>
                  <td>{Number(p.iva || 0).toLocaleString('es-CL')}</td>
                  <td>{Number(p.total || 0).toLocaleString('es-CL')}</td>
                  <td>{p?.detalles?.length || 0}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
