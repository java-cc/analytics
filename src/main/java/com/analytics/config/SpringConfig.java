package com.analytics.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
@EnableWebMvc
@ComponentScan("com.analytics")
@PropertySource("classpath:app.properties")
public class SpringConfig extends WebMvcConfigurerAdapter {

    @Resource
    private Environment environment;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    @Bean(name = "isDBInit")
    public AtomicBoolean isDBInit() {
        return new AtomicBoolean(false);
    }

    @Bean
    public DataSource dataSource() {
        final org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(environment.getProperty("db.driver"));
        ds.setUrl(environment.getProperty("db.url"));
        ds.setUsername(environment.getProperty("db.user"));
        ds.setPassword(environment.getProperty("db.password"));
        return ds;
    }
}
