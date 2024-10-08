package com.geithub.youssefagagg.htmltopdfservice;

import java.util.concurrent.Executor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class HtmlToPdfServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(HtmlToPdfServiceApplication.class, args);
  }
}
