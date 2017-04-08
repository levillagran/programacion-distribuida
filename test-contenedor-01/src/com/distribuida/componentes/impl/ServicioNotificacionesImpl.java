package com.distribuida.componentes.impl;

import com.distribuida.componentes.interfaces.ServicioNotificaciones;

public class ServicioNotificacionesImpl implements ServicioNotificaciones {

	@Override
	public void notificar(String msg) {
		System.out.println( "Notificando [" + msg + "]" );
	}

}
