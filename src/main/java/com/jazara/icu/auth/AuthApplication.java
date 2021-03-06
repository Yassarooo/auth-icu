package com.jazara.icu.auth;

import com.jazara.icu.auth.domain.Role;
import com.jazara.icu.auth.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableZuulProxy
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableDiscoveryClient
@EnableSwagger2
@EntityScan(basePackages = {"com.jazara.icu.auth"})
@EnableEncryptableProperties
public class AuthApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    RoleService roleService;

    @Override
    public void run(String... strings) throws Exception {

/*        Role uRole = new Role();
        uRole.setName("USER");
        uRole.setDescription("USER Role (Only Manage owned account)");
        roleService.CreateRole(uRole);
        Role aRole = new Role();
        aRole.setName("ADMIN");
        aRole.setDescription("ADMIN Role (Manage users)");
        roleService.CreateRole(aRole);*/
    }
}
