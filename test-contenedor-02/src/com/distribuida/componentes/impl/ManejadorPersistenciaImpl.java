package com.distribuida.componentes.impl;

import com.distribuida.componentes.interfaces.ManejadorPersistencia;
import com.distribuida.contenedor.anotaciones.Componente;

@Componente(nombre="manejadorPersistencia")
public class ManejadorPersistenciaImpl implements ManejadorPersistencia {

	public CuentaBancariaImpl buscarCuenta( String numero ) {
		
		System.out.println( "buscando cuenta [" + numero + "]" );
		
		return new CuentaBancariaImpl( numero );
	}
}
