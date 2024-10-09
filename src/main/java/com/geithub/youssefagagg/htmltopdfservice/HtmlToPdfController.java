package com.geithub.youssefagagg.htmltopdfservice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HtmlToPdfController {
  private final SaveHtmlToPdf saveHtmlToPdf;

  @PostMapping("/html-to-pdf")
  public ResponseEntity<Resource> generatePdf(@RequestBody List<String> htmlContents) {
    log.info("Generating PDF from list of HTML");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "document.pdf");
    return ResponseEntity.ok().headers(headers).body(saveHtmlToPdf.generatePdf(htmlContents));
  }

  @PostMapping("/html-to-pdf-single")
  public ResponseEntity<Resource> generatePdfFromHtml(@RequestBody String htmlContents) {
    log.info("Generating PDF from one HTML");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "document.pdf");
    return ResponseEntity.ok().headers(headers).body(saveHtmlToPdf.generatePdf(List.of(htmlContents)));
  }
}
