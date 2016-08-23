package kz.nicnbk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.text.SimpleDateFormat;

/**
 * Spring boot main application.
 *
 * Created by magzumov on 05.05.2016.
 */

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/news/**").allowedOrigins("http://localhost:8080", "http://localhost:8088",
                        "http://app.nicnbk.kz:8080", "http://app.nicnbk.kz");
                registry.addMapping("/m2s2/**").allowedOrigins("http://localhost:8080", "http://localhost:8088",
                        "http://app.nicnbk.kz:8080", "http://app.nicnbk.kz");
                registry.addMapping("/employee/**").allowedOrigins("http://localhost:8080", "http://localhost:8088",
                        "http://app.nicnbk.kz:8080", "http://app.nicnbk.kz");
                registry.addMapping("/lookup/**").allowedOrigins("http://localhost:8080", "http://localhost:8088",
                        "http://app.nicnbk.kz:8080", "http://app.nicnbk.kz");
                registry.addMapping("/bt/**").allowedOrigins("http://localhost:8080", "http://localhost:8088",
                        "http://app.nicnbk.kz:8080", "http://app.nicnbk.kz");
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.indentOutput(true).dateFormat(new SimpleDateFormat("dd-MM-yyyy"));
        return b;
    }
}
