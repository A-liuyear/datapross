import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server:{
    host: '0.0.0.0',
    port: 8081,
    hmr: true, //热更新
    open: true,
    proxy:{
      '/api':{
        target:'http://localhost:8089/',
        changeOrigin:true,
        rewrite:(path)=>path.replace(/^\/api/,'')
      }
    }
  },
  resolve:{
    alias:[
      {
        find: '@',
        replacement: resolve(__dirname,'src')
      }
    ]
  }
})
