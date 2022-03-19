package com.sd4.application;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@ComponentScan({"com.sd4.service", "com.sd4.controller"})
@EntityScan("com.sd4.model")
@EnableJpaRepositories("com.sd4.repository")
@EnableSwagger2
public class AssignmentTwo2022Application implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentTwo2022Application.class, args);
	}
        
        @Bean
        public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();                                           
        }
        
        @Bean
        public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
            return new BufferedImageHttpMessageConverter();
        }
        
       @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Register resource handler for images
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(2, TimeUnit.HOURS).cachePublic());
        }
}
