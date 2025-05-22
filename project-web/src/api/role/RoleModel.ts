export type SysRole={
    roleId:string,
    roleName:string,
    remark:string
}

//列表查询的参数
export type RoleListParam={
    currentPage:number,
    pageSize:number,
    roleName:string,
    total:number
}

export type RoleMenuParam={
   roleId:string,
   list:Array<string>
}