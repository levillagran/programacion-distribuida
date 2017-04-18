package com.distribuida.componentes.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuentaBancariaImpl {

	private Logger logger = LoggerFactory.getLogger( CuentaBancariaImpl.class );
	
	private String numero;
	
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public CuentaBancariaImpl( ) {
	}
	
	public CuentaBancariaImpl( String numero ) {
		this.numero = numero;
	}
	
	public void acreditar( float monto ) {
		logger.debug( "acreditar cuenta [" + numero + "] monto " + monto );
	}
	
	public void debitar( float monto ) {
		logger.debug( "debitar cuenta [" + numero + "] monto " + monto );
	}
}
