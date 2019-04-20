package com.bescalonadev.springboot.app.models.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final static String UPLOADS_FOLDER = "uploads";
	
	//Verifica que la foto que buscamos exista
	@Override
	public Resource load(String filename) throws MalformedURLException {
		Path pathFoto = getPath(filename);
		log.info("pathFoto: " + pathFoto);
		Resource recurso = null;
		recurso = new UrlResource(pathFoto.toUri());
		if (!recurso.exists() && !recurso.isReadable()) {
			throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
		}

		return recurso;
	}
	
	//Devuelve un id random + el nombre de la imagen, ademas copiamos la imagen en el directorio
	@Override
	public String copy(MultipartFile file) throws IOException {
		// Ahora creamos un directorio absoluto dentro del proyecto
		
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath = getPath(uniqueFilename);
		
		Files.copy(file.getInputStream(), rootPath);

		return uniqueFilename;
	}
	
	//Elimina la imagen del directorio
	@Override
	public boolean delete(String filename) {
		
		Path rootPath = getPath(filename);
		File archivo = rootPath.toFile();
		
		if(archivo.exists() && archivo.canRead()) {
			if(archivo.delete()) {
				return true;
			}
		}
		return false;
	}
	
	//Obtenemos la ruta absoluta de la imagen 
	public Path getPath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}
	
	//Borramos todas las imagenes del directorio
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
		
	}
	
	//Creamos el directorio
	@Override
	public void init() throws IOException {
		Files.createDirectories(Paths.get(UPLOADS_FOLDER));
		
	}

}
