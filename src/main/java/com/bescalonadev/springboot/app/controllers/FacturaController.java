package com.bescalonadev.springboot.app.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bescalonadev.springboot.app.models.dao.IFacturaDao;
import com.bescalonadev.springboot.app.models.dao.IProductoDao;
import com.bescalonadev.springboot.app.models.entity.Factura;
import com.bescalonadev.springboot.app.models.entity.ItemFactura;
import com.bescalonadev.springboot.app.models.entity.Producto;
import com.bescalonadev.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("factura")
public class FacturaController {
	
	@Autowired
	private IFacturaDao facturaDao;
	
	@Autowired
	private IProductoDao productoDao;

		
	//Devuelve todas las facturas paginadas para poder ser mostradas en la vista listar
	@RequestMapping(value = { "/listar", "/" }, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, HttpServletRequest request) {

		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Factura> facturas = facturaDao.findAll(pageRequest);     
		PageRender<Factura> pageRender = new PageRender<>("/listar", facturas);

		model.addAttribute("titulo", "Listado de facturas");
		model.addAttribute("page", pageRender);
		model.addAttribute("facturas", facturas);
		
		return "listar";
	} 
		
    //Devuelve una factura en especifico segun su id
	@Secured("ROLE_USER")	
	@GetMapping("/factura/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, 
			Model model, 
			RedirectAttributes flash) {

		Factura factura  = facturaDao.findById(id).orElse(null);
				
		if(factura == null) {
			flash.addFlashAttribute("error", "La factura no existe en la base de datos!");
			return "redirect:/listar";
		}
		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Factura: ".concat(factura.getId().toString()));
		return "factura/ver";
	}
	
	//Devuelve una factura vacia para poder ser completada en el formulario
	@Secured("ROLE_ADMIN")
	@GetMapping("/factura/form")
	public String crear(Map<String, Object> model,
			RedirectAttributes flash) {
		
		Factura factura = new Factura();

		model.put("factura", factura);
		model.put("titulo", "Crear Factura");

		return "/factura/form";
	} 
	
	//Recibe una factura desde el crear factura (sin id) o el editar factura (con id) y la guarda en la bd
	@Secured("ROLE_ADMIN")
	@PostMapping("/factura/form")
	public String guardar(@Valid Factura factura, 
			BindingResult result, 
			Model model,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad, 
			RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Crear Factura");
			return "factura/form";
		}

		if (itemId == null || itemId.length == 0) {
			model.addAttribute("titulo", "Crear Factura");
			model.addAttribute("error", "La factura debe tener productos");
			return "factura/form";
		}
		
		//Recorre todos los item factura asociados a la factura y disminuye el stock de productos dependiendo de la cantidad asociada a cada item
		for (int i = 0; i < itemId.length; i++) {
			Producto producto = productoDao.findById(itemId[i]).orElse(null);
			ItemFactura linea = new ItemFactura();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			//Si el producto esta asociado a una factura el atributo referencias aumenta en 1, esto sirve para tener un control de los productos vinculados con facturas vigentes, lo que permite restringir su eliminacion
			producto.setReferencias(producto.getReferencias()+1);
			producto.setStock((producto.getStock()-cantidad[i]));
			productoDao.save(producto);
			factura.addItemFactura(linea);
			
		}

		String mensajeFlash = (factura.getId() != null) ? "Factura editada con exito" : "Factura creada con exito";
		facturaDao.save(factura);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/";
	}
	
	//Recibe el id de una factura, la busca y luego la elimina, pero antes reduce en 1 el atributo referencias de cada producto asociado a la factura
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/factura/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
		if (id > 0) {
			Factura factura = facturaDao.findById(id).orElse(null);
			for(int i=0; i<factura.getItems().size();i++) {
				factura.getItems().get(i).getProducto().setReferencias(factura.getItems().get(i).getProducto().getReferencias()-1);
				productoDao.save(productoDao.findById(factura.getItems().get(i).getProducto().getId()).orElse(null));
			}
			facturaDao.deleteById(id);
			flash.addFlashAttribute("success", "La factura ha sido eliminada con exito");
		}
		return "redirect:/listar";
	} 
	
	//(NO IMPLEMENTADA) Busca una factura en especifico segun una id pasada por url, luego devuelve un objeto factura para ser cargado en el formulario (Aun no esta implementada, ya que se debe solucionar el problema de cargar los items con el stock y cantidad correspondiente)
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/factura/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {
		Factura factura = null;
		model.put("textoBoton", "Editar Factura");
		if (id > 0) {
			factura = facturaDao.findById(id).orElse(null);

			if (factura == null) {
				flash.addFlashAttribute("error", "La factura no existe en la base de datos");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID de la factura es incorrecto");
			return "redirect:/listar";
		}
		model.put("factura", factura);
		model.put("titulo", "Editar factura");
		
		return "/factura/form";
	} 
	
	//Devuelve productos que tengan el String "term" en parte de su nombre, para luego ser cargados en el formulario de la factura utilizando ajax
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/factura/cargar-productos/{term}", produces = { "application/json" })
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {
		return productoDao.findByNombre(term);
	} 
	
	//(NO IMPLEMENTADA) Busca una factura por su id y luego devuelve los items asociados a ella (Se planea utilizar en el editar), ademas aumenta el stock de los productos de cada item para balancearlo con la cantidad mostrada
	@GetMapping(value = "/cargar-items/", produces = { "application/json" })
	public @ResponseBody List<ItemFactura> cargarItems(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
		for(int i=0;i<facturaDao.findById(id).orElse(null).getItems().size();i++) {
			facturaDao.findById(id).orElse(null).getItems().get(i).getProducto().setStock(facturaDao.findById(id).orElse(null).getItems().get(i).getProducto().getStock() + facturaDao.findById(id).orElse(null).getItems().get(i).getCantidad());
		}
		
		return facturaDao.findById(id).orElse(null).getItems();
	}
}
