package com.distribuida.componentes.interfaces;

public interface TransaccionBancaria {
	public void transferir( String cuenta1, String cuenta2, float monto );
}
