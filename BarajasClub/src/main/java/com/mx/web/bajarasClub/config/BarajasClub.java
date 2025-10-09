package com.mx.web.bajarasClub.config;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Configuration
@SpringBootApplication(scanBasePackages = "com.mx.web.bajarasClub")
public class BarajasClub extends ResponseEntityExceptionHandler implements WebMvcConfigurer {

	@Value("${app.upload-dir}")
	private String uploadDir;

	public static void main(String[] args) {
		SpringApplication.run(BarajasClub.class, args);
	}
	
	
	 @PostConstruct
	  public void init() throws Exception {
	    // Crea /var/data/uploads (o la que definas) si no existe
	    Files.createDirectories(Paths.get(uploadDir));
	  }
	 
	 
	 
	 @Override
	  public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    // OJO: el patrón es URL (no ruta del SO). /uploads/** es lo que servirás en el navegador
	    String location = Paths.get(uploadDir).toAbsolutePath().toUri().toString(); // file:/var/data/uploads/
	    registry.addResourceHandler("/uploads/**")
	            .addResourceLocations(location);
	 }
	  

//  @Override
//  public void addResourceHandlers(ResourceHandlerRegistry registry) {
//      Path uploadDir = Paths.get("uploads");
//      String uploadPath = uploadDir.toFile().getAbsolutePath();
//      registry.addResourceHandler("/uploads/**")
//              .addResourceLocations("file:" + uploadPath + "/");
//  }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        Path uploadDir = Paths.get("uploads");
//        String uploadPath = uploadDir.toFile().getAbsolutePath();
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:" + uploadPath + "/");
//    }

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
		return ResponseEntity.badRequest().body("El archivo es demasiado grande. Tamaño máximo permitido: 10 MB");
	}

}
