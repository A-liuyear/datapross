/// <reference types="vite/client" />

declare module "*.vue" {
    // 这里定义了一个名为 *.vue 的模块声明，表示所有以.vue 结尾的文件都属于这个模块。
    // 接下来定义了这个模块的类型，引入了 DefineComponent 类型，这是 Vue 框架用于定义组件的类型。
    import { DefineComponent } from "vue"
    // 声明了一个名为 component 的常量，其类型为 DefineComponent<{}, {}, any>。
    // 这表示这个组件可以接受任何属性（{}），不包含方法（{}），并且可以渲染任何类型的模板。 
    const component: DefineComponent<{}, {}, any>
    // 使用 export default 将 component 常量导出。
    // 这意味着任何导入 *.vue 文件的模块，都将获得这个组件作为其默认导出。
    // 这样，其他模块就可以在自己的文件中使用这个组件，而不需要知道其具体实现细节。
    export default component
}

