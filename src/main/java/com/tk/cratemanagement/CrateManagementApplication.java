package com.tk.cratemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class CrateManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrateManagementApplication.class, args);
    }

}
