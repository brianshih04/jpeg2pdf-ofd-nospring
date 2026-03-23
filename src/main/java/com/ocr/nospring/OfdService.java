package com.ocr.nospring;

import org.ofdrw.layout.OFDDoc;
import org.ofdrw.layout.PageLayout;
import org.ofdrw.layout.VirtualPage;
import org.ofdrw.layout.element.Img;
import org.ofdrw.layout.element.Paragraph;
import org.ofdrw.layout.element.Span;
import org.ofdrw.layout.element.Position;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * OFD 服務 - 無 Spring Boot
 */
public class OfdService {
    
    /**
     * 生成多頁 OFD
     */
    public void generateMultiPageOfd(List<BufferedImage> images, List<List<OcrService.TextBlock>> allTextBlocks, File outputFile) throws Exception {
        
        if (images.size() != allTextBlocks.size()) {
            throw new IllegalArgumentException("Images and text blocks count mismatch");
        }
        
        // 臨時保存所有圖片
        Path tempDir = Files.createTempDirectory("ofd_multipage_");
        List<Path> tempImages = new ArrayList<>();
        
        try {
            try (OFDDoc ofdDoc = new OFDDoc(outputFile.toPath())) {
                
                // 處理每一頁
                for (int pageIndex = 0; pageIndex < images.size(); pageIndex++) {
                    BufferedImage image = images.get(pageIndex);
                    List<OcrService.TextBlock> textBlocks = allTextBlocks.get(pageIndex);
                    
                    // 保存圖片
                    Path tempImage = tempDir.resolve("page_" + pageIndex + ".png");
                    ImageIO.write(image, "PNG", tempImage.toFile());
                    tempImages.add(tempImage); // 記錄所有臨時圖片
                    
                    // 轉換坐標：像素 -> mm (假設 DPI = 72)
                    double widthMm = image.getWidth() * 25.4 / 72.0;
                    double heightMm = image.getHeight() * 25.4 / 72.0;
                    
                    // 創建頁面佈局
                    PageLayout pageLayout = new PageLayout(widthMm, heightMm);
                    pageLayout.setMargin(0d);
                    
                    // 創建虛擬頁面
                    VirtualPage vPage = new VirtualPage(pageLayout);
                    
                    // 添加背景圖片
                    Img img = new Img(tempImage);
                    img.setPosition(Position.Absolute)
                       .setX(0d)
                       .setY(0d)
                       .setWidth(widthMm)
                       .setHeight(heightMm);
                    vPage.add(img);
                    
                    // 添加不可見文字層
                    for (OcrService.TextBlock block : textBlocks) {
                        try {
                            String text = block.text;
                            if (text == null || text.trim().isEmpty()) continue;
                            
                            // OCR 邊界框
                            double ocrX = block.x * 25.4 / 72.0;
                            double ocrY = block.y * 25.4 / 72.0;
                            double ocrW = block.width * 25.4 / 72.0;
                            double ocrH = block.height * 25.4 / 72.0;
                            
                            // 字號
                            double fontSizeMm = ocrH * 0.75;
                            
                            // Y 軸轉換（OFD 使用 Y-down）
                            double paragraphY = heightMm - ocrY - ocrH;
                            
                            // 逐字符絕對定位
                            double currentX = ocrX;
                            
                            for (int charIdx = 0; charIdx < text.length(); charIdx++) {
                                String singleChar = String.valueOf(text.charAt(charIdx));
                                
                                Span span = new Span(singleChar);
                                span.setFontSize(fontSizeMm);
                                span.setColor(255, 255, 255); // 白色
                                
                                Paragraph p = new Paragraph();
                                p.add(span);
                                p.setX(currentX);
                                p.setY(paragraphY);
                                p.setOpacity(0.01); // 1% 透明度
                                
                                vPage.add(p);
                                
                                // 估算字符寬度（簡單估算）
                                double charWidthMm = fontSizeMm * 0.6;
                                currentX += charWidthMm;
                            }
                            
                        } catch (Exception e) {
                            System.err.println("    Page " + (pageIndex + 1) + " - Error drawing text: " + e.getMessage());
                        }
                    }
                    
                    // 添加頁面（不刪除圖片！）
                    ofdDoc.addVPage(vPage);
                }
            }
            // OFD 文檔已在此處關閉並生成完成
            
        } finally {
            // ✅ 在文檔完全生成後，再清理所有臨時圖片
            for (Path tempImage : tempImages) {
                Files.deleteIfExists(tempImage);
            }
            Files.deleteIfExists(tempDir);
        }
    }
    
    public void generateOfd(BufferedImage image, List<OcrService.TextBlock> textBlocks, File outputFile) throws Exception {
        
        // 臨時保存圖片
        Path tempDir = Files.createTempDirectory("ofd_");
        Path tempImage = tempDir.resolve("page.png");
        ImageIO.write(image, "PNG", tempImage.toFile());
        
        try (OFDDoc ofdDoc = new OFDDoc(outputFile.toPath())) {
            
            // 轉換坐標：像素 -> mm (假設 DPI = 72)
            double widthMm = image.getWidth() * 25.4 / 72.0;
            double heightMm = image.getHeight() * 25.4 / 72.0;
            
            // 創建頁面佈局
            PageLayout pageLayout = new PageLayout(widthMm, heightMm);
            pageLayout.setMargin(0d);
            
            // 創建虛擬頁面
            VirtualPage vPage = new VirtualPage(pageLayout);
            
            // 添加背景圖片
            Img img = new Img(tempImage);
            img.setPosition(Position.Absolute)
               .setX(0d)
               .setY(0d)
               .setWidth(widthMm)
               .setHeight(heightMm);
            vPage.add(img);
            
            // 添加不可見文字層
            for (OcrService.TextBlock block : textBlocks) {
                try {
                    String text = block.text;
                    if (text == null || text.trim().isEmpty()) continue;
                    
                    // OCR 邊界框
                    double ocrX = block.x * 25.4 / 72.0;
                    double ocrY = block.y * 25.4 / 72.0;
                    double ocrW = block.width * 25.4 / 72.0;
                    double ocrH = block.height * 25.4 / 72.0;
                    
                    // 字號
                    double fontSizeMm = ocrH * 0.75;
                    
                    // Y 軸轉換（OFD 使用 Y-down）
                    double paragraphY = heightMm - ocrY - ocrH;
                    
                    // 逐字符絕對定位
                    double currentX = ocrX;
                    
                    for (int charIdx = 0; charIdx < text.length(); charIdx++) {
                        String singleChar = String.valueOf(text.charAt(charIdx));
                        
                        Span span = new Span(singleChar);
                        span.setFontSize(fontSizeMm);
                        span.setColor(255, 255, 255); // 白色
                        
                        Paragraph p = new Paragraph();
                        p.add(span);
                        p.setX(currentX);
                        p.setY(paragraphY);
                        p.setOpacity(0.01); // 1% 透明度
                        
                        vPage.add(p);
                        
                        // 估算字符寬度（簡單估算）
                        double charWidthMm = fontSizeMm * 0.6;
                        currentX += charWidthMm;
                    }
                    
                } catch (Exception e) {
                    System.err.println("    Error drawing text: " + e.getMessage());
                }
            }
            
            // 添加頁面
            ofdDoc.addVPage(vPage);
        }
        
        // 清理臨時文件
        Files.deleteIfExists(tempImage);
        Files.deleteIfExists(tempDir);
    }
}
