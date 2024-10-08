package com.geithub.youssefagagg.htmltopdfservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HtmlToPdfServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(HtmlToPdfServiceApplication.class, args);
  }

}
