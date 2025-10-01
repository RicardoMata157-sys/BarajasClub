package com.mx.web.bajarasClub.config;

import java.nio.file.Paths;

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
public class TioAndresApplication extends ResponseEntityExceptionHandler implements WebMvcConfigurer  {
	
	
    public static void main(String[] args) {
        SpringApplication.run(TioAndresApplication.class, args);
    }
    
    
    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
      String location = Paths.get("/var/data/uploads").toAbsolutePath().toUri().toString();
      registry.addResourceHandler("/app/uploads/**")
              .addResourceLocations(location); // ej. file:/var/data/uploads/
    }
    
    
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        Path uploadDir = Paths.get("uploads");
//        String uploadPath = uploadDir.toFile().getAbsolutePath();
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:" + uploadPath + "/");
//    }
    
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity
                .badRequest()
                .body("El archivo es demasiado grande. Tamaño máximo permitido: 10 MB");
    }
    
    
 
    
    
    
    
}
