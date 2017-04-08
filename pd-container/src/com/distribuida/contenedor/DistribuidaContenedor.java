package com.distribuida.contenedor;

public interface DistribuidaContenedor {
	
	public void iniciar( );
	public void detener( );
	public <T> T lookup( String nombre, Class<T> type );
}
