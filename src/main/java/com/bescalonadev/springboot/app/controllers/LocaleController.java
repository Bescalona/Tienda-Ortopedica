package com.bescalonadev.springboot.app.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LocaleController {
	@GetMapping("/locale")
	//Obtiene la url de la pagina y redirige a ella
	public String locale(HttpServletRequest request) {
		String ultimaUrl = request.getHeader("referer");
		return "redirect:".concat(ultimaUrl);
	}
}
