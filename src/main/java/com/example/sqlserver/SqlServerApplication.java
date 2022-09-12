package com.example.sqlserver;

import com.example.sqlserver.Form.LoginServerSql;
import com.example.sqlserver.Form.SwingArea;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@SpringBootApplication
@Configuration
public class SqlServerApplication {
//    public SqlServerApplication() throws SQLException {
//        SwingArea.getInstance().initUI();
//    }

    public static void main(String[] args) {
//        SpringApplication.run(SqlServerApplication.class, args);
//        ApplicationContext ctx = new SpringApplicationBuilder(SqlServerApplication.class)
//                .headless(false).run(args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(SqlServerApplication.class);
        builder.headless(false).run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                new LoginServerSql();
            }
        };
    }

}
