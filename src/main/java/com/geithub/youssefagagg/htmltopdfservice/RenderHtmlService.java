package com.geithub.youssefagagg.htmltopdfservice;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.PrintsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.print.PageSize;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenderHtmlService {
  private final DriverPool driverPool;


  @Async("taskExecutor")
  @SneakyThrows
  public CompletableFuture<GeneratedPdf> savePdfFromHtml(int order, String htmlContent) {
    WebDriver driver = driverPool.borrowDriver();
    String uniqueID = UUID.randomUUID().toString();
    File tempHtmlFile = File.createTempFile("temp_" + uniqueID, ".html");
    try {
      log.info("Creating temporary file: {}", tempHtmlFile.getAbsolutePath());

      try (FileOutputStream fos = new FileOutputStream(tempHtmlFile)) {
        fos.write(htmlContent.getBytes());
      }

      driver.get("file://" + tempHtmlFile.getAbsolutePath());

      log.info("Waiting for page to render for file: {}", tempHtmlFile.getAbsolutePath());

      Wait<WebDriver> wait = new FluentWait<>(driver)
          .withTimeout(Duration.ofSeconds(60))
          .pollingEvery(Duration.ofSeconds(2))
          .ignoring(org.openqa.selenium.NoSuchElementException.class)
          .ignoring(org.openqa.selenium.TimeoutException.class);

      wait.until(d -> !d.findElement(By.tagName("h3")).getText().isBlank());
      log.info("Page rendered successfully for file: {}", tempHtmlFile.getAbsolutePath());

      PrintsPage printsPage = (PrintsPage) driver;
      PrintOptions printOptions = new PrintOptions();
      printOptions.setPageRanges("1");
      printOptions.setPageSize(new PageSize(30, 22));
      printOptions.setScale(1.0);

      String pdfBase64 = printsPage.print(printOptions).getContent();
      byte[] pdfData = Base64.getDecoder().decode(pdfBase64);
      ByteArrayResource byteArrayResource =new ByteArrayResource(pdfData);
      return CompletableFuture.completedFuture(new GeneratedPdf(order,byteArrayResource));
    } finally {
      driverPool.returnDriver(driver);
      tempHtmlFile.delete();
      log.info("Temporary file deleted: {}", tempHtmlFile.getAbsolutePath());
    }
  }
}
