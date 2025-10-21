import { Routes, Route } from "react-router-dom";
import Inicio from "./Pages/Inicio.jsx";
import Productos from "./Pages/Productos.jsx";
import Categorias from "./Pages/Categorias.jsx";
import Ofertas from "./Pages/Ofertas.jsx";
import Checkout from "./Pages/Checkout.jsx";
import CompraExitosa from "./Pages/CompraExitosa.jsx";
import CompraFallida from "./Pages/CompraFallida.jsx";
import AdminPanel from "./Pages/AdminPanel.jsx";
import Header from "./components/Header";
import Footer from "./components/Footer";
import "./App.css";
import Login from "./Pages/Login.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";

function App() {
  return (
    <div className="app-shell">
      <Header />
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Inicio />} />
          <Route path="/productos" element={<Productos />} />
          <Route path="/categorias" element={<Categorias />} />
          <Route path="/ofertas" element={<Ofertas />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/exito" element={<CompraExitosa />} />
          <Route path="/error" element={<CompraFallida />} />
          <Route path="/login" element={<Login />} />
          <Route path="/admin" element={
            <ProtectedRoute>
              <AdminPanel />
            </ProtectedRoute>
          } />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;
