package com.example.testkontur;

import com.example.testkontur.Service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Autowired
    private LinkService linkService;

    @Bean
    public LinkStorage linkStorage() {
        LinkStorage linkStorage = new LinkStorage(linkService);
        return linkStorage;
    }
}
