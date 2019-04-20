package com.bescalonadev.springboot.app.models.entity;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "Productos")
public class Producto implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="categoria_id")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Categoria categoria;
	
	@NotEmpty
	private String nombre;
	private String foto;
	private String descripcion;
	@NotNull
	private Double precio;
	@NotNull
	private Integer stock;

	@Column(name = "create_at")
	@Temporal(TemporalType.DATE)
	private Date createAt;
	
	//Si es mayor a cero siginifica que el producto forma parte de alguna factura, sirve para determinar si el producto puede ser eliminado
	private Integer referencias =0;

	@PrePersist
	public void prePersist() {
		createAt = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}  
	
    public Integer getReferencias() {
		return referencias;
	}

	public void setReferencias(Integer referencias) {
		this.referencias = referencias;
	}

	public String precioFormateado() { 
		
		Locale chileLocale = new Locale("es","CL"); // elegimos Chile
		NumberFormat nf = NumberFormat.getNumberInstance(chileLocale);

		return "$"+ nf.format(this.getPrecio().doubleValue());
	}

	private static final long serialVersionUID = 1L;

}
