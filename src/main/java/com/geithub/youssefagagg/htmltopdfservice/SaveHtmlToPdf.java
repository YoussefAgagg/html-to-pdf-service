package com.geithub.youssefagagg.htmltopdfservice;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveHtmlToPdf {

  private final RenderHtmlService renderHtmlService;
  @SneakyThrows
  public Resource generatePdf(List<String> htmlContents) {
    List<CompletableFuture<GeneratedPdf>> futures = new ArrayList<>();
    int order = 1;
    for (String html : htmlContents) {
      futures.add(renderHtmlService.savePdfFromHtml(order++,html));
    }
    // wait until all futures are completed
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[htmlContents.size()])).join();

    var generatedPdfs = futures.stream().map(CompletableFuture::join)
        .sorted(Comparator.comparingInt(GeneratedPdf::order))
        .map(GeneratedPdf::pdf)
        .toList();
    return PdfConcatenator.concatenate(generatedPdfs);
  }

}