import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // escucha en todas las interfaces (incluye localhost IPv4/IPv6)
    port: 5173,
    strictPort: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        // Mantén el prefijo /api para que /api/productos -> http://localhost:8080/api/productos
      },
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: './src/tests/setup.js',
    globals: true,
    include: ['src/**/*.{test,spec}.?(c|m)[jt]s?(x)']
  },
})
