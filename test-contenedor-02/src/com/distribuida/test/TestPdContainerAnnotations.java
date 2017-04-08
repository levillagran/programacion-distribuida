package com.distribuida.test;

import com.distribuida.componentes.interfaces.TransaccionBancaria;
import com.distribuida.contenedor.DistribuidaContenedor;
import com.distribuida.contenedor.impl.AnotacionesDistribuidaContenedor;

public class TestPdContainerAnnotations {

	public static final String  PACKAGE_TO_SCAN = "com.distribuida.componentes";
	
	public static void main(String[] args) {
		
		
		DistribuidaContenedor contenedor = new AnotacionesDistribuidaContenedor( PACKAGE_TO_SCAN );
		
		contenedor.iniciar( );
		
		TransaccionBancaria tb = contenedor.lookup( "transaccionBancaria", TransaccionBancaria.class );
		
		tb.transferir( "001", "002", 100.0f );
		
		contenedor.detener( );

	}

}
