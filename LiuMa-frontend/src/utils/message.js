import { Message } from 'element-ui';


const resetMessage =(options) => {
  let doms = document.getElementsByClassName('el-message');
  let canShow = true;
  for( let i=0; i<doms.length; i++){
    if(options.message == doms[i].getElementsByClassName('el-message__content')[0].innerHTML){
      canShow = false;
    }
  }
  if(doms.length === 0 || canShow){
    Message(options);
  }
};

['error','success','info','warning'].forEach(type => {
  resetMessage[type] = options => {
    if(typeof options === 'string') {
      options = {
        message:options
      }
    }
    options.type = type;
    return resetMessage(options);
  }
})

export const message = resetMessage;
