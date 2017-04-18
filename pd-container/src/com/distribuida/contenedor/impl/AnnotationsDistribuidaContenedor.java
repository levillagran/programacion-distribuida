package com.distribuida.contenedor.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.distribuida.contenedor.ComponentMetadata;
import com.distribuida.contenedor.DistribuidaContenedor;
import com.distribuida.contenedor.anotaciones.Componente;
import com.distribuida.contenedor.anotaciones.Depende;
import com.distribuida.contenedor.anotaciones.MiConstructor;
import com.distribuida.contenedor.anotaciones.MiDestructor;
import com.distribuida.contenedor.anotaciones.Transaccional;
import com.distribuida.contenedor.proxy.ComponenteTransaccionalBeanProxy;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class AnnotationsDistribuidaContenedor implements DistribuidaContenedor {
	
	private Logger logger = LoggerFactory.getLogger( AnnotationsDistribuidaContenedor.class );
	
	private String paqueteEscanear = "";
	
	protected Map<String, ComponentMetadata> componentesRegistrados = new HashMap<>();
	
	public AnnotationsDistribuidaContenedor( String paqueteEscanear ) {
		this.paqueteEscanear = paqueteEscanear;
	}
	
	public void iniciar( ) {
		escanearComponentes( );
	}
	
	public void detener( ) {
		
		componentesRegistrados.values().forEach( componente->{ 
		
			componente.getDestructores().forEach( m->{
				try {
					m.invoke( componente.getOriginalInstancia() );
				} catch (Exception e) {
					e.printStackTrace();
				}
			} );
		} );

	}
	
	public <T> T lookup( String nombre, Class<T> type ) {
		
		if( componentesRegistrados.containsKey(nombre)==false ) {
			throw new RuntimeException( "No existe el componente [" + nombre + "]" );
		}
		
		ComponentMetadata obj = componentesRegistrados.get( nombre );
		
		return type.cast( obj.getInstancia() );
	}
	
	private void escanearComponentes( ) {
		
		try {
			ClassPath classPath = ClassPath.from( AnnotationsDistribuidaContenedor.class.getClassLoader( ) );
			
			ImmutableSet<ClassInfo> clases = classPath.getTopLevelClassesRecursive( paqueteEscanear );
			
			for( ClassInfo cls:clases ) {
				
				String className = cls.getName( );
				
				Class<?> clase = Class.forName( className ); 
				
				Componente ann = clase.getAnnotation( Componente.class );
				
				if( ann!=null ) {
					
					// la clase tiene la anotación @Componente

					procesarComponente( ann, clase );
				}				
			}
			
			// registrar componentes internos
			ComponentMetadata contenedor = new ComponentMetadata( );
			
			contenedor.setInstancia( this );
			
			componentesRegistrados.put( "_contenedor", contenedor );

			// procesar dependencias
			procesarDependencias( );

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException( e );
		}
	}
	
	private void procesarComponente( Componente ann, Class<?> clase ) throws Exception {
		
		logger.debug( "Registrando clase '" + clase.getName( ) + "' como un componente." );
		
		ComponentMetadata data = new ComponentMetadata( );
		
		data.setNombre( ann.nombre( ) );
		data.setInstancia( clase.newInstance( ) );
	
		componentesRegistrados.put( ann.nombre( ), data );
	}
	
	private void procesarTransaccionalidad( ComponentMetadata metadata ) throws Exception {
		
		Class<?> objectClass = metadata.getInstancia().getClass();
		
		Transaccional trans = objectClass.getAnnotation( Transaccional.class );
		
		if( trans!=null ) {
			
			logger.debug( "Registrando proxy para bean transaccional '" + metadata.getNombre() + "'." );
			
			Class<?> interfaces[] = metadata.getInstancia().getClass().getInterfaces();
			
			Object proxiedObject = ComponenteTransaccionalBeanProxy.createProxy( metadata.getInstancia( ), interfaces[0] );
			
			Object original = metadata.getInstancia();
			
			metadata.setInstancia( proxiedObject );
			metadata.setOriginalInstancia( original );
		}
	}
	
	private void procesarFields( ComponentMetadata metadata ) throws IllegalArgumentException, IllegalAccessException {
		
		Object componente = metadata.getInstancia( );
		
		Class<?> clase = componente.getClass( );
		
		List<Field> fields = Arrays.stream( clase.getDeclaredFields( ) )
			.filter( f->f.isAnnotationPresent(Depende.class) )
			.collect( Collectors.toList() );
		
		for( Field f:fields ) {
			
			Depende dependencia = f.getAnnotation( Depende.class );
			
			String nombre = dependencia.nombre( );
			
			ComponentMetadata depObject = componentesRegistrados.get( nombre );
			
			if( depObject==null ) {
				throw new RuntimeException( "No existe el componente [" + nombre + "]" );
			}
			
			logger.debug( "Procesando dependencia " + metadata.getNombre( ) + "-->" + nombre );
			
			f.setAccessible( true );
			f.set( metadata.getInstancia(), depObject.getInstancia( ) );
		}
	}
	
	private void verificarParametros( Method m ) {
		if( m.getParameterCount()>0 ) {
			throw new RuntimeException( "El método no debe tener parámetros: " + m.getName( ) );
		}
		
		if( m.getReturnType()!=void.class ) {
			throw new RuntimeException( "El método no debe retornar ningún tipo de dato: " + m.getReturnType( ) );
		}
	}
	
	private void procesarMetodos( ComponentMetadata metadata ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		Object componente = metadata.getInstancia( );
		
		// Constructores
		List<Method> methods1 = Arrays.stream( componente.getClass().getMethods() )
			.filter( m->m.isAnnotationPresent( MiConstructor.class)  )
			.collect( Collectors.toList() );
		
		 methods1.forEach( m->{			 
			 verificarParametros( m );
			 metadata.getConstructores().add( m );
		});
		
		// Destructores
		 List<Method> methods2 = Arrays.stream( componente.getClass().getMethods() )
			.filter( m->m.isAnnotationPresent( MiDestructor.class)  )
			.collect( Collectors.toList() );

		methods2.forEach( m->{			
			verificarParametros( m );			
			metadata.getDestructores().add( m );
		});
		
		// invocart constructores
		for( Method m:metadata.getConstructores( ) ) {
			m.invoke( metadata.getInstancia( ) );
		}
	}
	
	private void procesarDependencias() throws Exception {
		
		for( String key:componentesRegistrados.keySet( ) ) {
			
			ComponentMetadata metadata = componentesRegistrados.get( key );
			
			procesarFields( metadata );
			
			procesarMetodos( metadata );

			procesarTransaccionalidad( metadata );
		}
	}

}
