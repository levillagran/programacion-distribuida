package com.distribuida.componentes.impl;

import com.distribuida.componentes.interfaces.ManejadorPersistencia;
import com.distribuida.componentes.interfaces.ServicioNotificaciones;
import com.distribuida.componentes.interfaces.TransaccionBancaria;
import com.distribuida.contenedor.anotaciones.Componente;
import com.distribuida.contenedor.anotaciones.Depende;

@Componente(nombre="transaccionBancaria")
public class TransaccionBancariaImpl implements TransaccionBancaria {

	@Depende(nombre="manejadorPersistencia") protected ManejadorPersistencia mp;
	
	@Depende(nombre="servicioNotificaciones") protected ServicioNotificaciones notificaciones;
	
	public void transferir( String cuenta1, String cuenta2, float monto ) {
		
		System.out.println( "transferir de [" + cuenta1 + "] a [" + cuenta2 + "] monto " + monto );
		
		CuentaBancariaImpl cb1 = mp.buscarCuenta( cuenta1 );
		CuentaBancariaImpl cb2 = mp.buscarCuenta( cuenta2 );
		
		cb1.acreditar( monto );
		cb2.debitar( monto );
		
		notificaciones.notificar( "Transaccion realizada." );
	}
	
}
