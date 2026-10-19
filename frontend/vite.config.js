import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    port: 3000,
    // Em desenvolvimento (npm run dev) as chamadas /api vao para o backend local.
    // Em Docker o mesmo caminho e resolvido pelo proxy do nginx (frontend/nginx.conf).
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
