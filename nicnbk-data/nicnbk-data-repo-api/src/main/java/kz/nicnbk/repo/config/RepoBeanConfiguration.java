package kz.nicnbk.repo.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by magzumov on 04.07.2016.
 */

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = {"kz.nicnbk.repo.model"})
@EnableJpaRepositories(basePackages = {"kz.nicnbk.repo.api"})
public class RepoBeanConfiguration {
}
