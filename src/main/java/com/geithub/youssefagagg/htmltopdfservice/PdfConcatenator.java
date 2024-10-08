package com.geithub.youssefagagg.htmltopdfservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * Concatenates multiple PDF files into a single PDF file.
 */
@Slf4j
public class PdfConcatenator {

  /**
   * Concatenates multiple PDF files into a single PDF file.
   *
   * @param pdfFiles The PDF files to concatenate.
   * @return The concatenated PDF file.
   */
  public static Resource concatenate(List<Resource> pdfFiles) {
    log.info("Concatenating PDF files");
    if (pdfFiles == null || pdfFiles.isEmpty()) {
      throw new IllegalArgumentException("No pdf files provided.");
    }
    PDFMergerUtility pdfMerger = new PDFMergerUtility();
    ByteArrayOutputStream outputPdfFile = new ByteArrayOutputStream();
    try {
      for (var pdfFile : pdfFiles) {
        try (InputStream is = pdfFile.getInputStream()) {
          pdfMerger.addSource(new RandomAccessReadBuffer(is));
        } catch (IOException e) {
          throw new RuntimeException("Error reading file ", e);
        }
      }
      pdfMerger.setDestinationStream(outputPdfFile);
      pdfMerger.mergeDocuments(null);
    } catch (IOException e) {
      throw new RuntimeException("Error merging documents", e);
    }
    return new ByteArrayResource(outputPdfFile.toByteArray());
  }

}