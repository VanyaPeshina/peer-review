package com.telerikacademy.finalprojectpeerreview;

import com.telerikacademy.finalprojectpeerreview.models.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class FinalProjectPeerReviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProjectPeerReviewApplication.class, args);
    }
}
