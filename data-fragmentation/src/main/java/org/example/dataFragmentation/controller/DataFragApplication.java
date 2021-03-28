package org.example.dataFragmentation.controller;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@EntityScan({"org.example.*"})
@ComponentScan(basePackages = {"org.example.*"})
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@EnableDubbo(scanBasePackages = "org.example.*")
public class DataFragApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(DataFragApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
