package com.bescalonadev.springboot.app.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.SessionFlashMapManager;

//Anotamos como component para poder utilizarlo en SpringSecurityConfig
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	//FlashMap funciona de manera similar a RedirectAttributes, solo que este ultimo solo se puede utilizar en los controladores
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		SessionFlashMapManager flashMapManager = new SessionFlashMapManager();
		
		FlashMap flashMap = new FlashMap();
		
		flashMap.put("success", "Hola "+authentication.getName()+ " haz iniciado sesión con éxito!");
		
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		
		if(authentication != null) {
			logger.info("El usuario '"+authentication.getName()+"' ha iniciado sesión con éxito");
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
}
