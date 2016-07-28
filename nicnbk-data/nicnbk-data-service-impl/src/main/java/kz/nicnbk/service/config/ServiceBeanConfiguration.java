package kz.nicnbk.service.config;

import org.dozer.DozerBeanMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magzumov on 07.07.2016.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"kz.nicnbk.service"})
@Import(value = kz.nicnbk.repo.config.RepoBeanConfiguration.class)
@EnableTransactionManagement
public class ServiceBeanConfiguration {

    /**
     * Dozer bean mapper for injection.
     *
     * @return
     */
    @Bean(name = "org.dozer.Mapper")
    public DozerBeanMapper dozerBean() {
        DozerBeanMapper dozerBean = new DozerBeanMapper();
        List mappings = new ArrayList();
        mappings.add("dozer/dozer-mapping.xml");
        dozerBean.setMappingFiles(mappings);
        return dozerBean;
    }
}
