package com.ocr.nospring;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Native Image 命令行版本
 * 
 * 使用簡單的命令行參數（不使用 JSON）
 * 更容易編譯成 Native Image
 */
public class NativeCli {
    
    private static final String VERSION = "3.0.0 (Native Image)";
    
    public static void main(String[] args) {
        try {
            System.setProperty("java.awt.headless", "true");
            
            if (args.length == 0) {
                printUsage();
                System.exit(0);
            }
            
            // 解析參數
            String input = null;
            String output = ".";
            String language = "chinese_cht";
            String format = "pdf";
            
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                
                if (arg.equals("-h") || arg.equals("--help")) {
                    printUsage();
                    System.exit(0);
                }
                
                if (arg.equals("-v") || arg.equals("--version")) {
                    System.out.println("JPEG2PDF-OFD v" + VERSION);
                    System.exit(0);
                }
                
                if (arg.equals("-i") || arg.equals("--input")) {
                    if (i + 1 < args.length) {
                        input = args[++i];
                    }
                    continue;
                }
                
                if (arg.equals("-o") || arg.equals("--output")) {
                    if (i + 1 < args.length) {
                        output = args[++i];
                    }
                    continue;
                }
                
                if (arg.equals("-l") || arg.equals("--lang")) {
                    if (i + 1 < args.length) {
                        language = args[++i];
                    }
                    continue;
                }
                
                if (arg.equals("-f") || arg.equals("--format")) {
                    if (i + 1 < args.length) {
                        format = args[++i];
                    }
                    continue;
                }
            }
            
            if (input == null) {
                System.err.println("ERROR: Input required (-i or --input)");
                printUsage();
                System.exit(1);
            }
            
            // 顯示配置
            System.out.println("========================================");
            System.out.println("  JPEG2PDF-OFD v" + VERSION);
            System.out.println("========================================");
            System.out.println();
            System.out.println("Input:    " + input);
            System.out.println("Output:   " + output);
            System.out.println("Language: " + language);
            System.out.println("Format:   " + format);
            System.out.println();
            
            // 創建服務
            Config config = new Config();
            OcrService ocrService = new OcrService();
            PdfService pdfService = new PdfService(config);
            TextService textService = new TextService();
            OfdService ofdService = new OfdService();
            
            // 獲取輸入文件
            List<File> inputFiles = getInputFiles(input);
            System.out.println("Found " + inputFiles.size() + " file(s)");
            
            if (inputFiles.isEmpty()) {
                System.err.println("ERROR: No files found");
                System.exit(1);
            }
            
            // 創建輸出目錄
            File outputDir = new File(output);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            // 處理文件
            int processed = 0;
            int failed = 0;
            
            for (File inputFile : inputFiles) {
                processed++;
                System.out.println();
                System.out.println("[" + processed + "/" + inputFiles.size() + "] " + inputFile.getName());
                
                try {
                    // 讀取圖片
                    BufferedImage image = ImageIO.read(inputFile);
                    if (image == null) {
                        System.err.println("  ERROR: Cannot read image");
                        failed++;
                        continue;
                    }
                    
                    System.out.println("  Image: " + image.getWidth() + "x" + image.getHeight());
                    
                    // OCR
                    System.out.println("  Running OCR...");
                    List<OcrService.TextBlock> textBlocks = ocrService.recognize(image, language);
                    System.out.println("  OK: " + textBlocks.size() + " blocks");
                    
                    // 生成輸出
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String baseName = getBaseName(inputFile.getName());
                    String outputFilename = baseName + "_" + timestamp;
                    
                    // PDF
                    if (format.contains("pdf") || format.contains("all")) {
                        File pdfFile = new File(outputDir, outputFilename + ".pdf");
                        pdfService.generatePdf(image, textBlocks, pdfFile);
                        System.out.println("  OK: PDF -> " + pdfFile.getName());
                    }
                    
                    // TXT
                    if (format.contains("txt") || format.contains("all")) {
                        File txtFile = new File(outputDir, outputFilename + ".txt");
                        textService.generateTxt(textBlocks, txtFile);
                        System.out.println("  OK: TXT -> " + txtFile.getName());
                    }
                    
                    // OFD
                    if (format.contains("ofd") || format.contains("all")) {
                        File ofdFile = new File(outputDir, outputFilename + ".ofd");
                        ofdService.generateOfd(image, textBlocks, ofdFile);
                        System.out.println("  OK: OFD -> " + ofdFile.getName());
                    }
                    
                } catch (Exception e) {
                    System.err.println("  ERROR: " + e.getMessage());
                    failed++;
                }
            }
            
            // 總結
            System.out.println();
            System.out.println("========================================");
            System.out.println("  Summary");
            System.out.println("========================================");
            System.out.println("Processed: " + processed);
            System.out.println("Failed:    " + failed);
            System.out.println();
            
            if (failed == 0) {
                System.out.println("SUCCESS: All files processed");
            } else {
                System.out.println("WARNING: Some files failed");
            }
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static List<File> getInputFiles(String input) {
        List<File> files = new ArrayList<>();
        
        File file = new File(input);
        
        if (!file.exists()) {
            // 可能是通配符
            File parent = file.getParentFile();
            if (parent == null) {
                parent = new File(".");
            }
            
            String name = file.getName();
            if (name.contains("*")) {
                // 通配符模式
                final String pattern = name;
                File[] matches = parent.listFiles((dir, name1) -> matchesPattern(name1, pattern));
                if (matches != null) {
                    files.addAll(Arrays.asList(matches));
                }
            }
            return files;
        }
        
        if (file.isFile()) {
            files.add(file);
        } else if (file.isDirectory()) {
            // 目錄中的所有圖片
            File[] images = file.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".png")
            );
            if (images != null) {
                files.addAll(Arrays.asList(images));
            }
        }
        
        return files;
    }
    
    private static boolean matchesPattern(String filename, String pattern) {
        if (pattern.equals("*") || pattern.equals("*.*")) {
            return true;
        }
        
        if (pattern.startsWith("*.")) {
            String ext = pattern.substring(1).toLowerCase();
            return filename.toLowerCase().endsWith(ext);
        }
        
        return filename.equals(pattern);
    }
    
    private static String getBaseName(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(0, dot) : filename;
    }
    
    private static void printUsage() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  JPEG2PDF-OFD v" + VERSION);
        System.out.println("========================================");
        System.out.println();
        System.out.println("USAGE:");
        System.out.println("  jpeg2pdf-ofd-native.exe -i <input> -o <output> [options]");
        System.out.println();
        System.out.println("ARGUMENTS:");
        System.out.println("  -i, --input <path>    Input file, folder, or pattern (required)");
        System.out.println("  -o, --output <path>   Output folder (default: current directory)");
        System.out.println();
        System.out.println("OPTIONS:");
        System.out.println("  -l, --lang <lang>     OCR language (default: chinese_cht)");
        System.out.println("  -f, --format <fmt>    Output format (default: pdf)");
        System.out.println("  -h, --help            Show this help");
        System.out.println("  -v, --version         Show version");
        System.out.println();
        System.out.println("FORMATS:");
        System.out.println("  pdf  - Searchable PDF");
        System.out.println("  ofd  - Searchable OFD");
        System.out.println("  txt  - Plain text");
        System.out.println("  all  - All formats");
        System.out.println();
        System.out.println("LANGUAGES:");
        System.out.println("  chinese_cht - Traditional Chinese (default)");
        System.out.println("  ch          - Simplified Chinese");
        System.out.println("  en          - English");
        System.out.println("  japan       - Japanese");
        System.out.println("  korean      - Korean");
        System.out.println("  (80+ languages supported)");
        System.out.println();
        System.out.println("EXAMPLES:");
        System.out.println("  # Single file");
        System.out.println("  jpeg2pdf-ofd-native.exe -i scan.jpg -o output/");
        System.out.println();
        System.out.println("  # Folder of images");
        System.out.println("  jpeg2pdf-ofd-native.exe -i images/ -o output/");
        System.out.println();
        System.out.println("  # Wildcard pattern");
        System.out.println("  jpeg2pdf-ofd-native.exe -i *.jpg -o output/");
        System.out.println();
        System.out.println("  # Specify language and format");
        System.out.println("  jpeg2pdf-ofd-native.exe -i image.jpg -o output/ -l en -f all");
        System.out.println();
        System.out.println("  # All formats");
        System.out.println("  jpeg2pdf-ofd-native.exe -i image.jpg -o output/ -f all");
        System.out.println();
    }
}
