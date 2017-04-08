package com.distribuida.contenedor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.distribuida.contenedor.DistribuidaContenedor;

public class ClasspathFileDistribuidaContenedor implements DistribuidaContenedor {
	
	private String nombreArchivoConfiguracion = "";
	
	protected Map<String, Object> componentesRegistrados = new HashMap<>();
	
	protected Map<String, Integer> componentesIndices = new HashMap<>();
	
	public ClasspathFileDistribuidaContenedor( String nombreArchivoConfiguracion ) {
		this.nombreArchivoConfiguracion = nombreArchivoConfiguracion;
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
		
		InputStream fis = null;
		
		try {
			
			Properties props = new Properties( );
			
			fis = ClasspathFileDistribuidaContenedor.class.getClassLoader().getResourceAsStream( nombreArchivoConfiguracion );
			
			props.load( fis );
			
			String _total = props.getProperty( "componentes.total", "0" );
			
			Integer total = Integer.valueOf( _total );
			
			for( int i=0; i<total; i++ )
			{
				String className = props.getProperty( String.format("componente.%d.clase", (i+1) ) );
				String nombreComponente = props.getProperty( String.format("componente.%d.nombre", (i+1) ) );
				
				Class<?> clase = Class.forName( className ); 
				
				componentesIndices.put( nombreComponente, (i+1) );
				
				procesarcomponente( nombreComponente, clase );
			}
			
			procesarDependencias( props );

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}
		finally {
			if( fis!=null ) {
				try {
					fis.close( );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void procesarcomponente( String nombreComponente, Class<?> clase ) throws Exception {
		
		System.out.println( "Registrando clase '" + clase.getName( ) + "' como un componente ==> " + nombreComponente );
	
		componentesRegistrados.put( nombreComponente, clase.newInstance( ) );
	}
	
	private void procesarDependencias( Properties props ) throws IllegalArgumentException, IllegalAccessException, SecurityException {
		
		try {
			
			for( String key:componentesRegistrados.keySet( ) ) {
				
				Object componente = componentesRegistrados.get( key );
				Integer indice = componentesIndices.get( key );
				
				//componente.1.dependencias=1
				String str = String.format( "componente.%d.dependencias", indice );
				
				String _dependencias = props.getProperty( str, "0" );
				
				Integer dependencias = Integer.valueOf( _dependencias );
				
				for( int i=0; i<dependencias; i++ ) {
					
					//componente.1.dependencia.1=mp
					//componente.1.dependencia.1.nombre=manejadorPersistencia
					
					str = String.format( "componente.%d.dependencia.%d" , indice, (i+1) );
					
					String dep = props.getProperty( str );
					
					Field field = componente.getClass().getDeclaredField( dep );
					
					String nombreDependencia = props.getProperty( str + ".nombre" );
					
					Object depObject = componentesRegistrados.get( nombreDependencia );
					
					if( depObject==null ) {
						throw new RuntimeException( "No existe el componente [" + nombreDependencia + "]" );
					}
					
					System.out.println( "Procesando dependencia " + key + "-->" + nombreDependencia );
					
					field.setAccessible( true );
					field.set( componente, depObject );
				}			
			}
		}
		catch( NoSuchFieldException ex ) {
			throw new RuntimeException( "No existe la variable de instancia: " + ex.getMessage() );
		}
	}

}
