package com.distribuida.contenedor.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.distribuida.contenedor.DistribuidaContenedor;
import com.distribuida.contenedor.anotaciones.Componente;
import com.distribuida.contenedor.anotaciones.Depende;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class AnotacionesDistribuidaContenedor implements DistribuidaContenedor {
	
	private String packageToScan = "";
	
	protected Map<String, Object> componentesRegistrados = new HashMap<>();
	
	public AnotacionesDistribuidaContenedor( String packageToScan ) {
		this.packageToScan = packageToScan;
	}
	
	public void iniciar( ) {
		escanearComponentes( );
	}
	
	public void detener( ) {
	}
	
	public <T> T lookup( String nombre, Class<T> type ) {
		
		if( componentesRegistrados.containsKey(nombre)==false ) {
			throw new RuntimeException( "No existe el componente [" + nombre + "]" );
		}
		
		Object obj = componentesRegistrados.get( nombre );
		
		return type.cast( obj );
	}
	
	
	private void escanearComponentes( ) {
		
		try {
			ClassPath classPath = ClassPath.from( AnotacionesDistribuidaContenedor.class.getClassLoader( ) );
			
			ImmutableSet<ClassInfo> clases = classPath.getTopLevelClassesRecursive( packageToScan );
			
			for( ClassInfo cls:clases ) {
				
				String className = cls.getName( );
				
				Class<?> clase = Class.forName( className ); 
				
				Componente ann = clase.getAnnotation( Componente.class );
				
				if( ann!=null ) {
					
					// la clase tiene la anotación @Componente

					procesarcomponente( ann, clase );
				}
				
			}
			
			procesarDependencias( );

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}
	}
	
	private void procesarcomponente( Componente ann, Class<?> clase ) throws Exception {
		
		System.out.println( "Registrando clase '" + clase.getName( ) + "' como un componente." );
	
		componentesRegistrados.put( ann.nombre( ), clase.newInstance( ) );
	}
	
	private void procesarDependencias() throws IllegalArgumentException, IllegalAccessException {
		
		for( String key:componentesRegistrados.keySet( ) ) {
			
			Object componente = componentesRegistrados.get( key );
			
			Field[] fields = componente.getClass().getDeclaredFields( );
			
			for( Field f:fields ) {
				
				Depende dependencia = f.getAnnotation( Depende.class );
				
				if( dependencia!=null ) {
					
					String nombre = dependencia.nombre( );
					
					Object depObject = componentesRegistrados.get( nombre );
					
					if( depObject==null ) {
						throw new RuntimeException( "No existe el componente [" + nombre + "]" );
					}
					
					System.out.println( "Procesando dependencia " + key + "-->" + nombre );
					
					f.setAccessible( true );
					f.set( componente, depObject );
				}
			}

		}
	}

}
