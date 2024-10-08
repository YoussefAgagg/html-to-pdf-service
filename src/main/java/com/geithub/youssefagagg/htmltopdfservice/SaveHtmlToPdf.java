import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveHtmlToPdf {
  @Value("${chrome.driver.path}")
  private String chromeDriverPath;

  @PostConstruct
  private void init() {
    System.setProperty("webdriver.chrome.driver", chromeDriverPath);
  }

  @Async
  public void generatePdf(List<String> htmlContents) throws InterruptedException {
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    for (String html : htmlContents) {
      futures.add(savePdfFromHtml(html));
    }
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }

  private static WebDriver createWebDriverInstance() {
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
    chromeOptions.addArguments("--disable-extensions");
    chromeOptions.addArguments("--start-maximized");
    chromeOptions.addArguments("--headless");

    return new ChromeDriver(chromeOptions);
  }

  @Async
  @SneakyThrows
  protected CompletableFuture<Void> savePdfFromHtml(String htmlContent) {
    WebDriver driver = createWebDriverInstance();
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
      printOptions.setPageSize(new PageSize(25, 18));
      printOptions.setScale(1.0);

      String pdfBase64 = printsPage.print(printOptions).getContent();
      byte[] pdfData = Base64.getDecoder().decode(pdfBase64);
      String fileName = "output_" + uniqueID + ".pdf";

      try (FileOutputStream fos = new FileOutputStream(fileName)) {
        fos.write(pdfData);
        log.info("PDF file created successfully: {}", fileName);
      }

    } finally {
      driver.quit();
      tempHtmlFile.delete();
      log.info("Temporary file deleted: {}", tempHtmlFile.getAbsolutePath());
    }
    return CompletableFuture.completedFuture(null);
  }
}