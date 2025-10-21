import api from "../lib/api.js";
import { productosData } from "../Data/productosData.js";

export async function cargarProductosIniciales() {
  try {
    console.log("Verificando productos en el backend...");
    const productosActuales = await api.get("/productos");
    
    if (productosActuales.data && productosActuales.data.length > 0) {
      console.log(`Ya hay ${productosActuales.data.length} productos en el backend.`);
      return productosActuales.data;
    }
    
    console.log("No hay productos. Cargando productos iniciales...");
    
    // Preparar productos sin el ID (el backend lo genera)
    const productosParaEnviar = productosData.map(({ id, ...resto }) => resto);
    
    const respuesta = await api.post("/productos/batch", productosParaEnviar);
    console.log(`✅ ${respuesta.data.length} productos cargados exitosamente.`);
    return respuesta.data;
  } catch (error) {
    console.error("Error al cargar productos:", error);
    console.log("📦 Usando productos locales como fallback");
    return productosData;
  }
}
