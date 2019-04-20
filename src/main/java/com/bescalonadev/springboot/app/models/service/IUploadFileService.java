package com.bescalonadev.springboot.app.models.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
	
	public Resource load (String filename) throws MalformedURLException;
	
	//Retorna el nombre cambiado de la imagen
	public String copy(MultipartFile file) throws IOException;
	
	//Retorna un boolean para saber si se elimino o no la imagen
	public boolean delete(String filename);
	
	//Borra la carpeta uploads con las imagenes
	public void deleteAll();
	
	//Crea nuevamente el directorio uploads
	public void init() throws IOException;
}
