package com.geithub.youssefagagg.htmltopdfservice;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DriverPool {
  @Value("${chrome.driver.path}")
  private String chromeDriverPath;
  @Value("${chrome.pool.size}")
  private int poolSize = 6;

  private final BlockingQueue<WebDriver> driverPool = new LinkedBlockingQueue<>(poolSize);

  @PostConstruct
  private void init() {
    System.setProperty("webdriver.chrome.driver", chromeDriverPath);
    for (int i = 0; i < poolSize; i++) {
      driverPool.add(createWebDriverInstance());
    }
  }
  private static WebDriver createWebDriverInstance() {
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
    chromeOptions.addArguments("--disable-extensions");
    chromeOptions.addArguments("--start-maximized");
    chromeOptions.addArguments("--headless");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--disable-dev-shm-usage");

    return new ChromeDriver(chromeOptions);
  }
  public WebDriver borrowDriver() throws InterruptedException {
    return driverPool.take();
  }

  public void returnDriver(WebDriver driver) {
    driverPool.offer(driver);
  }
}