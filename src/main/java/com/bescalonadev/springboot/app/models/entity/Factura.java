package com.bescalonadev.springboot.app.models.entity;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "Facturas")
public class Factura implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	private String nombre;
	
	private String observacion;

	@Temporal(TemporalType.DATE)
	@Column(name = "create_at")

	private Date createAt;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	// Con JoinColumn se indica la llave foranea, debido a que es una relacion en un
	// solo sentido
	@JoinColumn(name = "factura_id")
	private List<ItemFactura> items;

	public Factura() {
		this.items = new ArrayList<ItemFactura>();
	}

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

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public List<ItemFactura> getItems() {
		return items;
	}

	public void setItems(List<ItemFactura> items) {
		this.items = items;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void addItemFactura(ItemFactura item) {
		this.items.add(item);
	}

	public String getTotal() {
		Double total = 0.0;
		int size = items.size();

		for (int i = 0; i < size; i++) {
			total += items.get(i).calcularImporte();
		}
		
		Locale chileLocale = new Locale("es","CL"); // elegimos Chile
		NumberFormat nf = NumberFormat.getNumberInstance(chileLocale);

		return "$"+ nf.format(total);
	}

	private static final long serialVersionUID = 1L;
}
