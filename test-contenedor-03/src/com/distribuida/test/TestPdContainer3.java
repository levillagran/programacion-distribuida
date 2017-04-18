package com.distribuida.test;

import com.distribuida.componentes.interfaces.TransaccionBancaria;
import com.distribuida.contenedor.DistribuidaContenedor;
import com.distribuida.contenedor.impl.AnnotationsDistribuidaContenedor;

public class TestPdContainer3 {

	public static final String  PACKAGE_TO_SCAN = "com.distribuida.componentes";
	
	public static void main(String[] args) {
		
		
		DistribuidaContenedor contenedor = new AnnotationsDistribuidaContenedor( PACKAGE_TO_SCAN );
		
		contenedor.iniciar( );
		
		TransaccionBancaria tb = contenedor.lookup( "transaccionBancaria", TransaccionBancaria.class );
		
		System.out.println( "********Tipo de dato original del componente TRANSACCION_BANCARIA: " + tb.getClass().getName( ) );
		
		tb.transferir( "001", "002", 100.0f );
		
		System.out.println( "Transferencia realizada." );
		
		contenedor.detener( );

	}

}
