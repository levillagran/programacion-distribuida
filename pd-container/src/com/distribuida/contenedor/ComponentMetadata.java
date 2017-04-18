package com.distribuida.contenedor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ComponentMetadata {
	
	protected String nombre;
	
	protected List<Method> constructores = new ArrayList<>();
	protected List<Method> destructores = new ArrayList<>();
	
	protected Object instancia;
	protected Object originalInstancia = null;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Method> getConstructores() {
		return constructores;
	}

	public void setConstructores(List<Method> constructores) {
		this.constructores = constructores;
	}

	public List<Method> getDestructores() {
		return destructores;
	}

	public void setDestructores(List<Method> destructores) {
		this.destructores = destructores;
	}

	public Object getInstancia() {
		return instancia;
	}

	public void setInstancia(Object instancia) {
		this.instancia = instancia;
		originalInstancia = instancia;
	}

	public Object getOriginalInstancia() {
		return originalInstancia;
	}

	public void setOriginalInstancia(Object originalInstancia) {
		this.originalInstancia = originalInstancia;
	}
}
