package com.distribuida.componentes.interfaces;

import com.distribuida.componentes.impl.CuentaBancariaImpl;

public interface ManejadorPersistencia {
	public CuentaBancariaImpl buscarCuenta( String numero );
}
