package com.rili;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by CYM on 2017/3/6.
 */

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.rili")
@SpringBootApplication
public class Application {

    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.db")
    public DataSource dataSource() {
        // 必须指定type,此处不会读取配置中的type
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate cobarJdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
