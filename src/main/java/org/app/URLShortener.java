package org.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class URLShortener extends SpringBootServletInitializer {


    public static void main(String[] args) {
        SpringApplication.run(URLShortener.class, args);
    }
}
