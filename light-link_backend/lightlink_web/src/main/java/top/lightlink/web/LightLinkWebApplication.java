package top.lightlink.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"top.lightlink"})
@MapperScan(basePackages = "top.lightlink.mapper")
@EnableTransactionManagement
@EnableScheduling
public class LightLinkWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(LightLinkWebApplication.class, args);
    }
}
