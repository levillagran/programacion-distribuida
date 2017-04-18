package com.distribuida.componentes.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.distribuida.componentes.interfaces.ManejadorPersistencia;
import com.distribuida.componentes.interfaces.ServicioNotificaciones;
import com.distribuida.componentes.interfaces.TransaccionBancaria;
import com.distribuida.contenedor.DistribuidaContenedor;
import com.distribuida.contenedor.anotaciones.Componente;
import com.distribuida.contenedor.anotaciones.Depende;
import com.distribuida.contenedor.anotaciones.MiConstructor;
import com.distribuida.contenedor.anotaciones.MiDestructor;
import com.distribuida.contenedor.anotaciones.Transaccional;

@Componente(nombre="transaccionBancaria")
@Transaccional
public class TransaccionBancariaImpl implements TransaccionBancaria {

	private Logger logger = LoggerFactory.getLogger( TransaccionBancariaImpl.class );
	
	@Depende(nombre="_contenedor") DistribuidaContenedor contenedor;
	
	protected ManejadorPersistencia mp;
	protected ServicioNotificaciones notificaciones;
	
	public void transferir( String cuenta1, String cuenta2, float monto ) {
		
		logger.debug( "transferir de [{}] a [{}] {} ", cuenta1, cuenta2, monto );
		
		CuentaBancariaImpl cb1 = mp.buscarCuenta( cuenta1 );
		CuentaBancariaImpl cb2 = mp.buscarCuenta( cuenta2 );
		
		cb1.acreditar( monto );
		cb2.debitar( monto );
		
		notificaciones.notificar( "Transaccion realizada." );
	}
	
	@MiConstructor
	public void inicializar( ) {
		
		logger.debug( "mi constructor..." );
		
		mp = contenedor.lookup( "manejadorPersistencia", ManejadorPersistencia.class );
		notificaciones = contenedor.lookup( "servicioNotificaciones", ServicioNotificaciones.class );
	}
	
	@MiDestructor
	public void destruir( ) {
		logger.debug( "mi destructor..." );
	}
}
