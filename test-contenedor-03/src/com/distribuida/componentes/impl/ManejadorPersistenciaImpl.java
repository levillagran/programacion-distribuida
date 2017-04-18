package com.distribuida.componentes.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.distribuida.componentes.interfaces.ManejadorPersistencia;
import com.distribuida.contenedor.anotaciones.Componente;

@Componente(nombre="manejadorPersistencia")
public class ManejadorPersistenciaImpl implements ManejadorPersistencia {

	private Logger logger = LoggerFactory.getLogger( ManejadorPersistenciaImpl.class );
	
	public CuentaBancariaImpl buscarCuenta( String numero ) {
		
		logger.debug( "buscando cuenta [{}]", numero );
		
		return new CuentaBancariaImpl( numero );
	}
}
