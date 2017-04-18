package com.distribuida.contenedor.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponenteTransaccionalBeanProxy implements InvocationHandler {

	private Logger logger = LoggerFactory.getLogger( ComponenteTransaccionalBeanProxy.class );
	
	private Object currentObject;

	public ComponenteTransaccionalBeanProxy(Object obj) {
		this.currentObject = obj;
	}

	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {

		Object result = null;

		try {
			
			logger.debug( "[TRANSACION BEGIN]: {}", currentObject.getClass().getName() );
			
			result = m.invoke(currentObject, args);
			
			logger.debug( "[TRANSACION END]: {}", currentObject.getClass().getName() );
			
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		} catch (Exception e) {
			throw e;
		}

		return result;
	}
	
	public static Object createProxy( Object obj, Class<?> interf ) {
		
		Class<?> interfaces[] = {interf};
		
		InvocationHandler invocationHandler = new ComponenteTransaccionalBeanProxy(obj);
		
		return Proxy.newProxyInstance( obj.getClass().getClassLoader(), interfaces, invocationHandler );
	}
}
