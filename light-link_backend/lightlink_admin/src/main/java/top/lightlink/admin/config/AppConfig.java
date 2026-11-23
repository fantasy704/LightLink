package top.lightlink.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${admin.key}")
    private String adminKey;

    public String getAdminKey() {
        return adminKey;
    }
}
