export type User={
    userId:string,
    userName:string,
    password:string,
    phone:string,
    email:string,
    sex:string,
    nickName:string,
    pointNum:number,
    roleId:string
}

export type UserListParam={
    phone:string;
    nickName:string;
    pageSize:number;
    currentPage:number;
    total:number;
}

export type loginParam={
    userName:string;
    password:string;
    code:string;
}


export type registerParam={
    userName:string;
    password:string;
}

export type MenuParam={
    userId:string;
    roleId:string;
}


export type UpdatePwdParam={
    userId:string;
    oldPassword:string;
    password:string;
}