export type DataFile={
    dataId:string,
    fileName:string,
    price:number,
    remark:string,
    status:string,
}
export type DataFileParam={
    dataId:string,
    status:string,
}

export type DataListParam={
    dataId:string,
    fileName:string,
    searchText:string,
    priceMin:string,
    priceMax:string,
    dateStart: string, // 日期范围
    dateEnd: string, // 日期范围
    rating:string, // 评分
    sortBy:string,
    category:string, // 分类
}


export type Order={
    dataId:string,
    payAmount:string
}

