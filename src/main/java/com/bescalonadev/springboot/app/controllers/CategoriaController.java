package com.bescalonadev.springboot.app.controllers;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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

import com.bescalonadev.springboot.app.models.dao.ICategoriaDao;
import com.bescalonadev.springboot.app.models.dao.IProductoDao;
import com.bescalonadev.springboot.app.models.entity.Categoria;
import com.bescalonadev.springboot.app.models.entity.Producto;
import com.bescalonadev.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("categoria")
public class CategoriaController {
	
	@Autowired
	ICategoriaDao categoriaDao; 
	
	@Autowired
	IProductoDao productoDao; 
	
	//Devuelve todas las categorias paginadas para poder ser mostradas en la vista categoria/listar
	@RequestMapping(value ="/categoria/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, HttpServletRequest request) {

		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Categoria> categorias = categoriaDao.findAll(pageRequest);     
		PageRender<Categoria> pageRender = new PageRender<>("/categoria/listar", categorias);

		model.addAttribute("titulo", "Listado de categorias");
		model.addAttribute("page", pageRender);
		model.addAttribute("categorias", categorias);
		
		return "/categoria/listar";
	}  
	
	//Devuelve una categoria en especifico segun su id y todos los productos asociados a dicha categoria
	@Secured("ROLE_USER")
	@GetMapping("/categoria/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, 
			Model model, 
			RedirectAttributes flash) {
		
		List<Producto> productos= productoDao.findByCategoria(id);
		
		Categoria categoria  = categoriaDao.findById(id).orElse(null);
				
		if(categoria == null) {
			flash.addFlashAttribute("error", "La categoria no existe en la base de datos!");
			return "redirect:/categoria/listar";
		}
		model.addAttribute("productos", productos);
		model.addAttribute("categoria", categoria);
		model.addAttribute("titulo", "Categoria: ".concat(categoria.getId().toString()));
		return "categoria/ver";
	}
	
	//Devuelve una categoria vacia para poder ser completada en el formulario
	@Secured("ROLE_ADMIN")
	@GetMapping("/categoria/form")
	public String crear(Map<String, Object> model,
			RedirectAttributes flash) {
		
		Categoria categoria = new Categoria();

		model.put("categoria", categoria);
		model.put("titulo", "Crear Categoria");

		return "/categoria/form";
	} 
	
	//Recibe una categoria desde el crear categoria (sin id) o el editar categoria (con id) y la guarda en la bd
	@Secured("ROLE_ADMIN")
	@PostMapping("/categoria/form")
	public String guardar(@Valid Categoria categoria, 
			BindingResult result, 
			Model model,
			RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Crear Categoria");
			return "categoria/form";
		}

		String mensajeFlash = (categoria.getId() != null) ? "Categoria editada con exito" : "Categoria creada con exito";
		categoriaDao.save(categoria);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/categoria/listar";
	} 
	
	//Recibe el id de una categoria la busca y luego la elimina, pero antes lista todos los productos asociados a la categoria a eliminar y los asigna a la categoria "Sin categoria asignada"
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/categoria/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
		if (id > 1) {
			//Categoria "Sin categoria asignada"
			Categoria catVacia=categoriaDao.findById(1L).orElse(null);
			//Productos asociados a la categoria a eliminar
			List<Producto> productos= productoDao.findByCategoria(id);
			for(int i=0; i<productos.size();i++) {
				Producto prodSinCategoria = productoDao.findById(productos.get(i).getId()).orElse(null);
				prodSinCategoria.setCategoria(catVacia);
				productoDao.save(prodSinCategoria);
			}
			categoriaDao.deleteById(id);
			flash.addFlashAttribute("success", "La categoria ha sido eliminada con exito");
		} 
		//La categoria "Sin categoria asignada", no puede ser eliminada, ya que actua como almacenador de los productos que quedan sin categoria
		if(id==1) {
			flash.addFlashAttribute("error", "Esta categoria no puede ser eliminada");
		}
		return "redirect:/categoria/listar";
	} 
	
	//Busca una categoria en especifico segun una id pasada por url, luego devuelve un objeto categoria para ser cargado en el formulario
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/categoria/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {
		Categoria categoria = null;
		model.put("textoBoton", "Editar Categoria");
		if (id > 0) {
			categoria = categoriaDao.findById(id).orElse(null);

			if (categoria == null) {
				flash.addFlashAttribute("error", "La categoria no existe en la base de datos");
				return "redirect:/categoria/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID de la categoria es incorrecto");
			return "redirect:/listar";
		}
		model.put("categoria", categoria);
		model.put("titulo", "Editar categoria");
		
		return "/categoria/form";
	}
	
	//Devuelve todas las categorias existentes, este metodo es utilizado en algunos select html los cuales son rellenados utilizando javascript (factura/js/autocomplete-productos)
	@GetMapping(value = "/cargar-categorias/", produces = { "application/json" })
	public @ResponseBody List<Categoria> cargarCategorias() {
		return (List<Categoria>) categoriaDao.findAll();
	}
}
