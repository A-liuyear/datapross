import { createApp } from 'vue'
import router from './router/index'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import piniaPersist from 'pinia-plugin-persist'
import { createPinia } from 'pinia'
import confirmFunc from './utils/confirm'
//import {permission}  from './directive/permission'
import hasPerm from './directive/hasPermission'
import * as echarts from 'echarts'
import './permission'
import './index.css'
//import './style.css'
import App from './App.vue'

//createApp(App).mount('#app')
const app=createApp(App);
//app.directive('permission',permission)
const pinia = createPinia()
app.use(router)
//app.use(ElementPlus)
app.use(ElementPlus, {
  locale: zhCn,
})
app.use(pinia)

pinia.use(piniaPersist)
app.mount('#app')
//全局注册图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }

//全局挂载 需要在hooks里添加 useInstance.ts
app.config.globalProperties.$confirmFunc=confirmFunc
app.config.globalProperties.$hasPerm=hasPerm
app.config.globalProperties.$echarts=echarts

