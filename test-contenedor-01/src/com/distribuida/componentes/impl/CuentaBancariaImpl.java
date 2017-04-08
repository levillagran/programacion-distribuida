package com.distribuida.componentes.impl;

public class CuentaBancariaImpl {

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
		System.out.println( "acreditar cuenta [" + numero + "] monto " + monto );
	}
	
	public void debitar( float monto ) {
		System.out.println( "debitar cuenta [" + numero + "] monto " + monto );
	}
}
