package com.bescalonadev.springboot.app.models.entity;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "facturas_items")
public class ItemFactura implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer cantidad;
	
	@ManyToOne(fetch=FetchType.LAZY)
	//Con JoinColumn se indica la llave foranea, aunque se podria omitir, ya que en este casos se crea de forma explicita
	@JoinColumn(name="producto_id")
	//PARA RESOLVER LO DEL EDITAR
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Producto producto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	public Double calcularImporte() {
		return cantidad.doubleValue() * producto.getPrecio();
	}
	
	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}
	
	public String totalFormateado() { 
		
		Locale chileLocale = new Locale("es","CL"); // elegimos Chile
		NumberFormat nf = NumberFormat.getNumberInstance(chileLocale);

		return "$"+ nf.format(cantidad.doubleValue() * producto.getPrecio());
	} 
	
	public String precioFormateado() { 
		
		Locale chileLocale = new Locale("es","CL"); // elegimos Chile
		NumberFormat nf = NumberFormat.getNumberInstance(chileLocale);

		return "$"+ nf.format(producto.getPrecio().doubleValue());
	}

	private static final long serialVersionUID = 1L;

}
