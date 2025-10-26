import React, { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { obtenerProductos, crearProducto, actualizarProducto, eliminarProducto, obtenerCategoriasStats } from "../api/apiProductos";
import { obtenerUsuarios, crearUsuario, actualizarUsuario, eliminarUsuario } from "../api/apiUsuarios";
import { obtenerPedidos } from "../api/apiPedidos";
import "./AdminPanel.css";

export default function AdminPanelClean() {
  const [productos, setProductos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [pedidos, setPedidos] = useState([]);
  const [catStats, setCatStats] = useState([]);
  const [vista, setVista] = useState("dashboard");
  const [form, setForm] = useState({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });
  const [enviando, setEnviando] = useState(false);
  const [mensaje, setMensaje] = useState({ tipo: "", texto: "" });

  const [editando, setEditando] = useState(null);
  const [formEdit, setFormEdit] = useState({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });

  // Estados para gestión de usuarios
  const [vistaUsuarios, setVistaUsuarios] = useState("lista");
  const [usuarioEditando, setUsuarioEditando] = useState(null);
  const [formUsuario, setFormUsuario] = useState({ nombre: "", apellido: "", correo: "", celular: "" });

  const navigate = useNavigate();

  useEffect(() => { cargar(); }, []);

  async function cargar() {
    try {
      const [prods, usrs, peds, cats] = await Promise.all([
        obtenerProductos().catch(() => []),
        obtenerUsuarios().catch(() => []),
        obtenerPedidos().catch(() => []),
        obtenerCategoriasStats().catch(() => []),
      ]);
      setProductos(Array.isArray(prods) ? prods : []);
      setUsuarios(Array.isArray(usrs) ? usrs : []);
      setPedidos(Array.isArray(peds) ? peds : []);
      setCatStats(Array.isArray(cats) ? cats : []);
    } catch {
      setProductos([]); setUsuarios([]); setPedidos([]); setCatStats([]);
    }
  }

  const lowStock = useMemo(() => productos.filter(p => Number(p.stock) > 0 && Number(p.stock) <= 5).length, [productos]);

  async function handleCrear(e) {
    e.preventDefault();
    setMensaje({ tipo: "", texto: "" });
    if (!form.nombre?.trim()) return setMensaje({ tipo: "error", texto: "El nombre es obligatorio." });
    if (!form.precio || Number(form.precio) <= 0) return setMensaje({ tipo: "error", texto: "El precio debe ser mayor a 0." });
    setEnviando(true);
    try {
      await crearProducto(form);
      setMensaje({ tipo: "ok", texto: "Producto creado con éxito." });
      setForm({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 });
      cargar();
    } catch (e) {
      const msg = e?.response?.status === 401 ? "No autorizado. Inicia sesión con el usuario Admin del backend." : "No se pudo crear el producto. Intenta nuevamente.";
      setMensaje({ tipo: "error", texto: msg });
    } finally { setEnviando(false); }
  }

  async function handleEliminar(id) { if (!confirm("¿Eliminar producto?")) return; await eliminarProducto(id); cargar(); }

  function iniciarEdicion(p) {
    setEditando(p.id);
    setFormEdit({ nombre: p.nombre || "", descripcion: p.descripcion || "", precio: p.precio || 0, categoria: p.categoria || "Postres", imagen: p.imagen || "", oferta: !!p.oferta, stock: Number(p.stock) || 0 });
  }
  function cancelarEdicion() { setEditando(null); setFormEdit({ nombre: "", descripcion: "", precio: 0, categoria: "Postres", imagen: "", oferta: false, stock: 0 }); }

  async function guardarEdicion() {
    setMensaje({ tipo: "", texto: "" });
    if (!formEdit.nombre?.trim()) return setMensaje({ tipo: "error", texto: "El nombre es obligatorio." });
    if (!formEdit.precio || Number(formEdit.precio) <= 0) return setMensaje({ tipo: "error", texto: "El precio debe ser mayor a 0." });
    try { await actualizarProducto(editando, formEdit); setMensaje({ tipo: "ok", texto: "Producto actualizado con éxito." }); cancelarEdicion(); cargar(); }
    catch (e) { const msg = e?.response?.status === 401 ? "No autorizado. Inicia sesión con el usuario Admin del backend." : "No se pudo actualizar el producto. Intenta nuevamente."; setMensaje({ tipo: "error", texto: msg }); }
  }

  // Funciones para gestión de usuarios
  async function handleCrearUsuario(e) {
    e.preventDefault();
    setMensaje({ tipo: "", texto: "" });
    if (!formUsuario.nombre?.trim() || !formUsuario.correo?.trim()) {
      return setMensaje({ tipo: "error", texto: "Nombre y correo son obligatorios." });
    }
    setEnviando(true);
    try {
      await crearUsuario(formUsuario);
      setMensaje({ tipo: "ok", texto: "Usuario creado con éxito." });
      setFormUsuario({ nombre: "", apellido: "", correo: "", celular: "" });
      setVistaUsuarios("lista");
      cargar();
    } catch (e) {
      setMensaje({ tipo: "error", texto: "No se pudo crear el usuario." });
    } finally { setEnviando(false); }
  }

  async function handleEditarUsuario(e) {
    e.preventDefault();
    setMensaje({ tipo: "", texto: "" });
    if (!formUsuario.nombre?.trim() || !formUsuario.correo?.trim()) {
      return setMensaje({ tipo: "error", texto: "Nombre y correo son obligatorios." });
    }
    try {
      await actualizarUsuario(usuarioEditando.id, formUsuario);
      setMensaje({ tipo: "ok", texto: "Usuario actualizado." });
      setVistaUsuarios("lista");
      setUsuarioEditando(null);
      setFormUsuario({ nombre: "", apellido: "", correo: "", celular: "" });
      cargar();
    } catch (e) {
      setMensaje({ tipo: "error", texto: "No se pudo actualizar." });
    }
  }

  async function handleEliminarUsuario(id) {
    if (!confirm("¿Eliminar usuario?")) return;
    try {
      await eliminarUsuario(id);
      setMensaje({ tipo: "ok", texto: "Usuario eliminado." });
      cargar();
    } catch (e) {
      setMensaje({ tipo: "error", texto: "No se pudo eliminar." });
    }
  }

  function iniciarEdicionUsuario(u) {
    setUsuarioEditando(u);
    setFormUsuario({ nombre: u.nombre, apellido: u.apellido, correo: u.correo, celular: u.celular });
    setVistaUsuarios("editar");
  }

  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <div className="admin-brand">Admin</div>
        <nav>
          <button className={`nav-item ${vista === "dashboard" ? "active" : ""}`} onClick={() => setVista("dashboard")}>Dashboard</button>
          <button className={`nav-item ${vista === "productos" ? "active" : ""}`} onClick={() => setVista("productos")}>Productos</button>
          <button className={`nav-item ${vista === "usuarios" ? "active" : ""}`} onClick={() => setVista("usuarios")}>Usuarios</button>
          <button className={`nav-item ${vista === "pedidos" ? "active" : ""}`} onClick={() => setVista("pedidos")}>Órdenes</button>
          <button className={`nav-item ${vista === "categorias" ? "active" : ""}`} onClick={() => setVista("categorias")}>Categorías</button>
        </nav>
        <div className="admin-sidebar-footer">
          <button className="nav-item outline" onClick={() => navigate("/")}>Tienda</button>
        </div>
      </aside>

      <main className="admin-content">
        <h2 className="mb-3">Panel Administrador</h2>
        {mensaje.texto && (<div className={`alert ${mensaje.tipo === 'ok' ? 'alert-success' : 'alert-danger'}`} role="alert">{mensaje.texto}</div>)}

        {vista === "dashboard" && (
          <>
            <section className="cards-grid">
              <div className="stat-card primary" onClick={() => setVista("pedidos")}> 
                <div className="stat-title">Compras</div>
                <div className="stat-value">{pedidos.length || 0}</div>
                <div className="stat-sub">Pedidos totales</div>
              </div>
              <div className="stat-card success" onClick={() => setVista("productos")}> 
                <div className="stat-title">Productos</div>
                <div className="stat-value">{productos.length || 0}</div>
                <div className="stat-sub">Inventario actual</div>
              </div>
              <div className="stat-card warning" onClick={() => setVista("productos")}> 
                <div className="stat-title">Stock bajo</div>
                <div className="stat-value">{lowStock}</div>
                <div className="stat-sub">Productos ≤ 5</div>
              </div>
              <div className="stat-card info" onClick={() => setVista("usuarios")}> 
                <div className="stat-title">Usuarios</div>
                <div className="stat-value">{usuarios.length || 0}</div>
                <div className="stat-sub">Registrados</div>
              </div>
            </section>

            {catStats?.length > 0 && (
              <section className="panel">
                <h4>Productos por categoría</h4>
                <div className="category-list">
                  {catStats.map((c) => (
                    <div key={String(c.nombre)} className="category-item">
                      <span>{c.nombre}</span>
                      <strong>{c.cantidad}</strong>
                    </div>
                  ))}
                </div>
              </section>
            )}
          </>
        )}

        {vista === "productos" && (
          <>
            <h4 className="mt-2">Crear Nuevo Producto</h4>
            <form onSubmit={handleCrear} className="mb-4 p-3 border rounded bg-light">
              <div className="row g-2">
                <div className="col-md-4">
                  <input className="form-control" placeholder="Nombre" value={form.nombre} onChange={e => setForm({...form, nombre: e.target.value})} required/>
                </div>
                <div className="col-md-2">
                  <input className="form-control" placeholder="Precio" type="number" value={form.precio} onChange={e => setForm({...form, precio: Number(e.target.value)})} required/>
                </div>
                <div className="col-md-2">
                  <select className="form-select" value={form.categoria} onChange={e => setForm({...form, categoria: e.target.value})}>
                    <option value="Postres">Postres</option>
                    <option value="Bebidas">Bebidas</option>
                  </select>
                </div>
                <div className="col-md-2">
                  <input className="form-control" placeholder="Imagen (URL)" value={form.imagen} onChange={e => setForm({...form, imagen: e.target.value})}/>
                </div>
                <div className="col-md-1 d-flex align-items-center gap-2">
                  <label className="form-check-label">Oferta</label>
                  <input className="form-check-input" type="checkbox" checked={form.oferta} onChange={e => setForm({...form, oferta: e.target.checked})}/>
                </div>
                <div className="col-md-1">
                  <input className="form-control" placeholder="Stock" type="number" value={form.stock} onChange={e => setForm({...form, stock: Number(e.target.value)})}/>
                </div>
                <div className="col-md-12">
                  <textarea className="form-control" placeholder="Descripción" value={form.descripcion} onChange={e => setForm({...form, descripcion: e.target.value})} />
                </div>
              </div>
              <div className="mt-3">
                <button disabled={enviando} className="btn btn-primary">{enviando ? 'Enviando...' : 'Crear'}</button>
              </div>
            </form>

            <section className="panel">
              <h4>Inventario ({productos.length})</h4>
              <div className="table-responsive">
                <table className="table table-sm table-striped">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Nombre</th>
                      <th>Precio</th>
                      <th>Categoría</th>
                      <th>Stock</th>
                      <th>Oferta</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {productos.map(p => (
                      <tr key={p.id}>
                        {editando === p.id ? (
                          <>
                            <td>{p.id}</td>
                            <td><input className="form-control form-control-sm" value={formEdit.nombre} onChange={e => setFormEdit({...formEdit, nombre: e.target.value})}/></td>
                            <td><input className="form-control form-control-sm" type="number" value={formEdit.precio} onChange={e => setFormEdit({...formEdit, precio: Number(e.target.value)})}/></td>
                            <td>
                              <select className="form-select form-select-sm" value={formEdit.categoria} onChange={e => setFormEdit({...formEdit, categoria: e.target.value})}>
                                <option value="Postres">Postres</option>
                                <option value="Bebidas">Bebidas</option>
                              </select>
                            </td>
                            <td><input className="form-control form-control-sm" type="number" value={formEdit.stock} onChange={e => setFormEdit({...formEdit, stock: Number(e.target.value)})}/></td>
                            <td>
                              <input className="form-check-input" type="checkbox" checked={formEdit.oferta} onChange={e => setFormEdit({...formEdit, oferta: e.target.checked})}/>
                            </td>
                            <td>
                              <button className="btn btn-sm btn-success me-1" onClick={guardarEdicion}>Guardar</button>
                              <button className="btn btn-sm btn-secondary" onClick={cancelarEdicion}>Cancelar</button>
                            </td>
                          </>
                        ) : (
                          <>
                            <td>{p.id}</td>
                            <td>{p.nombre}</td>
                            <td>{Number(p.precio || 0).toLocaleString('es-CL')}</td>
                            <td>{p.categoria}</td>
                            <td>{p.stock}</td>
                            <td>{p.oferta ? "✅" : "❌"}</td>
                            <td>
                              <button className="btn btn-sm btn-warning me-1" onClick={() => iniciarEdicion(p)}>Editar</button>
                              <button className="btn btn-sm btn-danger" onClick={() => handleEliminar(p.id)}>Eliminar</button>
                            </td>
                          </>
                        )}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          </>
        )}

        {vista === "usuarios" && (
          <section className="panel">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h4>Usuarios ({usuarios.length})</h4>
              {vistaUsuarios === "lista" && (
                <button className="btn btn-primary" onClick={() => setVistaUsuarios("crear")}>
                  + Crear Usuario
                </button>
              )}
            </div>

            {vistaUsuarios === "lista" && (
              <div className="table-responsive">
                <table className="table table-sm table-striped">
                  <thead>
                    <tr><th>#</th><th>Nombre</th><th>Correo</th><th>Celular</th><th>Acciones</th></tr>
                  </thead>
                  <tbody>
                    {usuarios.map(u => (
                      <tr key={u.id}>
                        <td>{u.id}</td>
                        <td>{u.nombre} {u.apellido}</td>
                        <td>{u.correo}</td>
                        <td>{u.celular}</td>
                        <td>
                          <button className="btn btn-sm btn-warning me-1" onClick={() => iniciarEdicionUsuario(u)}>Editar</button>
                          <button className="btn btn-sm btn-danger" onClick={() => handleEliminarUsuario(u.id)}>Eliminar</button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {(vistaUsuarios === "crear" || vistaUsuarios === "editar") && (
              <form onSubmit={vistaUsuarios === "crear" ? handleCrearUsuario : handleEditarUsuario} className="p-3 border rounded bg-light">
                <h5>{vistaUsuarios === "crear" ? "Crear Usuario" : "Editar Usuario"}</h5>
                <div className="row g-2">
                  <div className="col-md-6">
                    <input className="form-control" placeholder="Nombre" value={formUsuario.nombre} 
                      onChange={e => setFormUsuario({...formUsuario, nombre: e.target.value})} required/>
                  </div>
                  <div className="col-md-6">
                    <input className="form-control" placeholder="Apellido" value={formUsuario.apellido} 
                      onChange={e => setFormUsuario({...formUsuario, apellido: e.target.value})}/>
                  </div>
                  <div className="col-md-6">
                    <input className="form-control" placeholder="Correo" type="email" value={formUsuario.correo} 
                      onChange={e => setFormUsuario({...formUsuario, correo: e.target.value})} required/>
                  </div>
                  <div className="col-md-6">
                    <input className="form-control" placeholder="Celular" value={formUsuario.celular} 
                      onChange={e => setFormUsuario({...formUsuario, celular: e.target.value})}/>
                  </div>
                </div>
                <div className="mt-3">
                  <button type="submit" className="btn btn-success me-2" disabled={enviando}>
                    {enviando ? "Guardando..." : "Guardar"}
                  </button>
                  <button type="button" className="btn btn-secondary" onClick={() => {
                    setVistaUsuarios("lista");
                    setUsuarioEditando(null);
                    setFormUsuario({ nombre: "", apellido: "", correo: "", celular: "" });
                  }}>Cancelar</button>
                </div>
              </form>
            )}
          </section>
        )}

        {vista === "pedidos" && (
          <section className="panel">
            <h4>Órdenes ({pedidos.length})</h4>
            <div className="table-responsive">
              <table className="table table-sm table-striped">
                <thead><tr><th>#</th><th>Fecha</th><th>Total</th><th>Usuario</th><th>Detalles</th></tr></thead>
                <tbody>
                  {pedidos.map(p => (
                    <tr key={p.id}><td>{p.id}</td><td>{p.fecha}</td><td>{Number(p.total || 0).toLocaleString('es-CL')}</td><td>{p?.usuario?.nombre || '-'}</td><td>{p?.detalles?.length || 0}</td></tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}

        {vista === "categorias" && (
          <section className="panel">
            <h4>Categorías</h4>
            {catStats?.length ? (
              <div className="category-list">
                {catStats.map((c) => (
                  <div key={String(c.nombre)} className="category-item">
                    <span>{c.nombre}</span>
                    <strong>{c.cantidad}</strong>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-muted">Sin datos de categorías.</p>
            )}
          </section>
        )}

      </main>
    </div>
  );
}
