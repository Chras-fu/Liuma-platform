/**
 * app测试元素定位方式
 **/
 export const locateBys = {
    android: [
        { label: "Xpath定位", value: "XPATH" },
        { label: "属性定位", value: "PROP" }
    ],
    apple: [
        { label: "Xpath定位", value: "XPATH" },
        { label: "属性定位", value: "PROP" },
        { label: "Predicate定位", value: "PRED" },
        { label: "ClassChain定位", value: "CLASS" }
    ]
}

/**
 * app测试定位属性值
 **/
export const locateProps = {
    android: ["text","textContains","textMatches","textStartsWith","className","classNameMatches","description","descriptionContains","descriptionMatches","descriptionStartsWith","checkable","checked","clickable","longClickable","scrollable","enabled","focusable","focused","selected","packageName","packageNameMatches","resourceId","resourceIdMatches"],
    apple: ["id","className", "name", "value", "label"]
}  

/**
 * app测试键位定义
 **/
 export const systemKeys = {
    android: [
        {id: "home", name: "home键"},
        {id: "back", name: "返回键"},
        {id: "left", name: "左键"},
        {id: "right", name: "右键"},
        {id: "up", name: "上键"},
        {id: "down", name: "下键"},
        {id: "center", name: "选中键"},
        {id: "menu", name: "菜单键"},
        {id: "search", name: "搜索键"},
        {id: "enter", name: "enter键"},
        {id: "delete", name: "删除键"},
        {id: "recent", name: "最近活动键"},
        {id: "volume_up", name: "音量+键"},
        {id: "volume_down", name: "音量-键"},
        {id: "volume_mute", name: "静音键"},
        {id: "camera", name: "相机键"},
        {id: "power", name: "电源键"}
    ],
    apple: [
        {id: "home", name: "home键"},
        {id: "volume_up", name: "音量+键"},
        {id: "volume_down", name: "音量-键"}
    ],
}  

/**
 * app测试元素属性
 **/
 export const elementProps = {
    android: ["checked","scrollable","selected","enabled", "focused", "focusable", "clickable", "longClickable", "checkable"],
    apple: ["visible", "enabled", "displayed", "accessible"],
}  