package com.bescalonadev.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bescalonadev.springboot.app.models.dao.IProductoDao;
import com.bescalonadev.springboot.app.models.entity.Producto;
import com.bescalonadev.springboot.app.util.paginator.PageRender;
import com.bescalonadev.springboot.app.models.service.IUploadFileService;


@Controller
@SessionAttributes("producto")
public class ProductoController {
	
	private final Log logger = LogFactory.getLog(WebSecurityConfigurerAdapter.class);
	
	@Autowired
	IProductoDao productoDao;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Secured("ROLE_USER")
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}
	
	//Devuelve todos los productos paginados para poder ser mostrados en la vista producto/listar
	@RequestMapping(value = "/producto/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, HttpServletRequest request) {
	
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Producto> productos = productoDao.findAll(pageRequest);     
		PageRender<Producto> pageRender = new PageRender<>("/producto/listar", productos);

		model.addAttribute("titulo", "Listado de productos");
		model.addAttribute("page", pageRender);
		model.addAttribute("productos", productos);
		return "/producto/listar";
	}
	
	//Devuelve un producto en especifico segun su id
	@Secured("ROLE_USER")
	@GetMapping("/producto/ver/{id}")
	public String ver(@PathVariable(value="id") Long id, 
			Model model, 
			RedirectAttributes flash) {
		
		Producto producto  = productoDao.findById(id).orElse(null);
				
		if(producto == null) {
			flash.addFlashAttribute("error", "El producto no existe en la base de datos!");
			return "redirect:/producto/listar";
		}
		
		model.addAttribute("producto", producto);
		model.addAttribute("titulo", "Producto: ".concat(producto.getId().toString()));
		return "producto/ver";
	}
	
	//Devuelve un producto vacio para poder ser completada en el formulario
	@Secured("ROLE_ADMIN")
	@GetMapping("/producto/form")
	public String crear(Map<String, Object> model,
			RedirectAttributes flash) {
		
		Producto producto = new Producto();

		model.put("producto", producto);
		model.put("titulo", "Crear Producto");

		return "/producto/form";
	}  
	
	//Recibe un producto desde el crear producto (sin id) o el editar producto (con id) y lo guarda en la bd
	@Secured("ROLE_ADMIN")
	@PostMapping("/producto/form")
	public String guardar(@Valid Producto producto, 
			BindingResult result, 
			Model model,
			@RequestParam("file") MultipartFile foto,
			RedirectAttributes flash,
			SessionStatus status) {
		
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Crear Producto");
			return "producto/form";
		} 
		
		if (!foto.isEmpty()) {
			// Reemplazar imagen por una nueva
			if (producto.getId() != null && producto.getId() > 0 && producto.getFoto() != null
					&& producto.getFoto().length() > 0) {
				uploadFileService.delete(producto.getFoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			producto.setFoto(uniqueFilename);
		}

		String mensajeFlash = (producto.getId() != null) ? "Producto editado con exito" : "Producto creado con exito";
		productoDao.save(producto);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/producto/listar";
	} 
	
	//Recibe el id de un producto, lo busca y luego lo elimina, pero antes verifica si el producto esta asociado a alguna factura a traves del atributo "referencias" si este atributo es mayor a cero significa que se encuentra asociado a una factura y no puede ser eliminado
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/producto/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
		if (id > 0) {
			Producto producto = productoDao.findById(id).orElse(null);
			if(producto.getReferencias()<=0) {
				productoDao.deleteById(id);
				flash.addFlashAttribute("success", "El producto ha sido eliminado con exito");
			}else {
				flash.addFlashAttribute("error", "El producto no se puede eliminar, ya que se encuentra asociado a facturas vigentes");
			}		
		
		}
		return "redirect:/producto/listar";
	}  
	
	//Busca un producto en especifico segun una id pasada por url, luego devuelve un objeto producto para ser cargado en el formulario 
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/producto/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash, Locale locale) {
		Producto producto = null;
		model.put("textoBoton", "Editar Producto");
		if (id > 0) {
			producto = productoDao.findById(id).orElse(null);

			if (producto == null) {
				flash.addFlashAttribute("error", "El producto no existe en la base de datos");
				return "redirect:/categoria/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del producto es incorrecto");
			return "redirect:/listar";
		}
		model.put("producto", producto);
		model.put("titulo", "Editar producto");
		
		return "/producto/form";
	}
	
	//Devuelve todos los productos asociados a una categoria (id), es utilizado en varios formularios (la carga se realiza a traves de ajax)
	@GetMapping(value = "/cargar-productos/", produces = { "application/json" })
	public @ResponseBody List<Producto> cargarProductos(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
		return productoDao.findByCategoria(id);
	} 
	
	//Devuelve un producto en especifico segun una id, es utilizado por el select producto de los formularios y a traves de ajax agrega un item factura con el producto devuelto
	@GetMapping(value = "/agregar-producto/", produces = { "application/json" })
	public @ResponseBody Producto agregarProducto(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
		return productoDao.findById(id).orElse(null);
	}
	
	//Devuelve el stock de un producto en especifico, util para mostrar el stock actual en el formulario para crear facturas
	@GetMapping(value = "/recuperar-stock/", produces = { "application/json" })
	public @ResponseBody Integer recuperarStock(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
		return productoDao.findById(id).orElse(null).getStock();
	}
	
	//Devuelve el nombre de un producto en especifico, se utiliza en /factura/js/autocomplete-productos para indicar el nombre de un producto que no tiene stock disponible
	@GetMapping(value = "/recuperar-nombre/")
	public @ResponseBody String recuperarNombre(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) {
		logger.info("NOMBRE DEL PRODUCTO: "+ productoDao.findById(id).orElse(null).getNombre());
		return productoDao.findById(id).orElse(null).getNombre();
	}

}
