import { defineConfig } from 'vite';

export default defineConfig({
  base: './',
  build: {
    target: 'es2015',
    cssCodeSplit: false,
    outDir: 'dist',
    emptyOutDir: true
  }
});
