package com.distribuida.componentes.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.distribuida.componentes.interfaces.ServicioNotificaciones;
import com.distribuida.contenedor.anotaciones.Componente;
import com.distribuida.contenedor.anotaciones.Transaccional;

@Componente(nombre="servicioNotificaciones")
@Transaccional
public class ServicioNotificacionesImpl implements ServicioNotificaciones {

	private Logger logger = LoggerFactory.getLogger( ServicioNotificacionesImpl.class );
	
	@Override
	public void notificar(String msg) {
		logger.debug( "Notificando [" + msg + "]" );
	}
}
