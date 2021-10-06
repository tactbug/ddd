package com.tactbug.ddd.product.assist.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.HashMap;
import java.util.Map;

import static io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.OPTIONS;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

/**
 * @Author tactbug
 * @Email tactbug@Gmail.com
 * @Time 2021/10/7 0:33
 */
@Configuration
public class PostgreConnectionConfigurer extends AbstractR2dbcConfiguration {

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        Map<String, String> options = new HashMap<>();
        options.put("lock_timeout", "10s");
        options.put("statement_timeout", "5m");
        return ConnectionFactories.get(builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "192.168.1.200")
                .option(PORT, 5432)
                .option(USER, "postgres")
                .option(PASSWORD, "900922")
                .option(DATABASE, "postgres")
                .option(OPTIONS, options)
                .build());
    }
}
