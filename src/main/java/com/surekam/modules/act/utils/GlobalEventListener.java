package com.surekam.modules.act.utils;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.delegate.event.ActivitiEvent;  
import org.activiti.engine.delegate.event.ActivitiEventListener;  	

import com.surekam.common.utils.SpringContextHolder;
import com.surekam.modules.act.service.EventHandler;

/** 
 * Activiti的全局事件监听器，即所有事件均需要在这里统一分发处理 
 * @author ludang 
 * @copyright
 * 
 */  
public class GlobalEventListener implements ActivitiEventListener{  	
   
	//更换为以下模式，可以防止Spring容器启动时，ProcessEngine尚未创建，而业务类中又使用了这个引用  
	private Map<String,String> handlers=new HashMap<String, String>();  
	@Override  
	public void onEvent(ActivitiEvent event) {  
		String eventType=event.getType().name();  
		  //根据事件的类型ID,找到对应的事件处理器  
		  String eventHandlerBeanId=handlers.get(eventType);  
		  if(eventHandlerBeanId!=null){  
		   EventHandler handler=(EventHandler)SpringContextHolder.getBean(eventHandlerBeanId);  
		   handler.handle(event);  
		  }  
	}
  
 @Override  
 public boolean isFailOnException() {  
  return false;  
 }  
 
 public Map<String, String> getHandlers() {  
	  return handlers;  
	 }  
	  
 public void setHandlers(Map<String, String> handlers) {  
  this.handlers = handlers;  
 }  
   
}  