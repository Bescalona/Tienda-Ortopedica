package com.bescalonadev.springboot.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bescalonadev.springboot.app.models.service.IUploadFileService;

@SpringBootApplication
public class SpringBootOrtopediaApplication implements CommandLineRunner{
	
	//Para eliminar la carpeta upload y luego iniciarla cada vez que compile el proyecto (se debe quitar en produccion)
	@Autowired
	IUploadFileService uploadFileService;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootOrtopediaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		//Elimina la carpeta uploads
		uploadFileService.deleteAll();
		//vuelve a crear la carpeta
		uploadFileService.init();
		
	}

}
