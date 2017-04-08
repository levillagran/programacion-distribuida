package com.distribuida.test;

import com.distribuida.componentes.interfaces.TransaccionBancaria;
import com.distribuida.contenedor.DistribuidaContenedor;
import com.distribuida.contenedor.impl.ClasspathFileDistribuidaContenedor;

public class TestContenedorFile {

	public static final String  CONFIG_FILE = "com/distribuida/test/config.properties";
	
	public static void main(String[] args) {
				
		DistribuidaContenedor contenedor = new ClasspathFileDistribuidaContenedor( CONFIG_FILE );
		
		contenedor.iniciar( );
		
		TransaccionBancaria tb = contenedor.lookup( "transaccionBancaria", TransaccionBancaria.class );
		
		tb.transferir( "001", "002", 100.0f );
		
		contenedor.detener( );

	}

}
