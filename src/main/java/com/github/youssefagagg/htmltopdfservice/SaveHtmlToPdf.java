package com.github.youssefagagg.htmltopdfservice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveHtmlToPdf {

  private final RenderHtmlService renderHtmlService;

  @SneakyThrows
  public Resource generatePdf(List<String> htmlContents) {
    log.info("Generating PDF from list of HTML");
    List<CompletableFuture<GeneratedPdf>> futures = new ArrayList<>();
    int order = 1;
    for (String html : htmlContents) {
      futures.add(renderHtmlService.savePdfFromHtml(order++, html));
    }
    log.info("Waiting for all futures to complete");
    // wait until all futures are completed
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[htmlContents.size()])).join();

    log.info("All futures completed");
    log.info("getting generated PDFs");
    var generatedPdfs = futures.stream().map(CompletableFuture::join)
        .sorted(Comparator.comparingInt(GeneratedPdf::order))
        .map(GeneratedPdf::pdf)
        .toList();
    if (generatedPdfs.size() == 1) {
      log.info("Returning single PDF");
      return generatedPdfs.getFirst();
    }
    log.info("Concatenating PDFs");
    return PdfConcatenator.concatenate(generatedPdfs);
  }

}