
export function toJsonPath(arr, json, basePath) {
  // 生成jsonpath
  const type = validateType(json)
  if (type === 'object') {
      for(let key in json) {
          const item = {
              key,
              path: `${basePath}.${key}`,
              childValue: json[key]
          }
          const childType = validateType(json[key])
          item.type = childType
          if (childType === 'object' || childType === 'array') {
              item.leaf = true
              item.children = []
              toJsonPath(item.children, json[key], item.path);
          } else {
              item.leaf = false;
              item.value = json[key];
          }
          arr.push(item);
      }
  } else if (type === 'array') {
      json.forEach((item,index) => {
          const childType = validateType(item);
          const obj = {
              key: index,
              childValue: item
          };
          obj.type = childType;
          obj.path = `${basePath}[${index}]`;
          if (childType === 'object' || childType === 'array') {
              obj.leaf = true;
              obj.children = [];
              toJsonPath(obj.children, item, obj.path);
          } else {
              obj.value = item;
              obj.leaf = false;
          }
          arr.push(obj);
      })
  }
}

export function validateType(val) {
  // 校验JSON数据类型
  const type = typeof val
  if (type === 'object') {
      if (Array.isArray(val)) {
      return 'array';
      } else if (val === null) {
      return 'null';
      } else {
      return 'object';
      }
  } else {
      switch(type) {
      case 'boolean':
          return 'Boolean';
      case 'string':
          return 'String';
      case 'number':
        var str = val+"";
        if(str.indexOf(".")==-1){
            return 'Int';
        }else{
            return 'Float';
        }
      default:
          return 'error';
      }
  }
}
