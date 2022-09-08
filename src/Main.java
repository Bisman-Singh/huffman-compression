import java.io.*;
import java.util.*;

public class Main {

    private static final String SAMPLE_TEXT =
        "Huffman coding is a lossless data compression algorithm. The idea is to assign " +
        "variable-length codes to input characters, with shorter codes assigned to more frequent " +
        "characters. This reduces the overall number of bits needed to represent the data. " +
        "The algorithm was developed by David A. Huffman while he was a Sc.D. student at MIT, " +
        "and published in the 1952 paper 'A Method for the Construction of Minimum-Redundancy Codes'. " +
        "Huffman coding is widely used in many compression formats including DEFLATE (used in gzip " +
        "and PNG), JPEG, and MP3. It is optimal among all methods that encode each symbol separately.";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            switch (args[0]) {
                case "-c":
                    if (args.length < 3) {
                        System.err.println("Usage: -c <input> <output>");
                        System.exit(1);
                    }
                    compressFile(args[1], args[2]);
                    break;
                case "-d":
                    if (args.length < 3) {
                        System.err.println("Usage: -d <input> <output>");
                        System.exit(1);
                    }
                    decompressFile(args[1], args[2]);
                    break;
                case "--demo":
                    runDemo();
                    break;
                default:
                    printUsage();
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void compressFile(String input, String output) throws IOException {
        File inputFile = new File(input);
        if (!inputFile.exists()) {
            System.err.println("Input file not found: " + input);
            System.exit(1);
        }

        long originalSize = inputFile.length();
        System.out.println("Compressing: " + input);
        System.out.println("Original size: " + formatSize(originalSize));

        long startTime = System.nanoTime();
        long compressedSize = Compressor.compress(input, output);
        long elapsed = System.nanoTime() - startTime;

        double ratio = originalSize > 0 ? (double) compressedSize / originalSize * 100 : 0;
        double savings = 100 - ratio;

        System.out.println("Compressed size: " + formatSize(compressedSize));
        System.out.printf("Compression ratio: %.1f%% (%.1f%% savings)%n", ratio, savings);
        System.out.printf("Time: %.3f ms%n", elapsed / 1_000_000.0);
        System.out.println("Output: " + output);
    }

    private static void decompressFile(String input, String output) throws IOException {
        File inputFile = new File(input);
        if (!inputFile.exists()) {
            System.err.println("Input file not found: " + input);
            System.exit(1);
        }

        long compressedSize = inputFile.length();
        System.out.println("Decompressing: " + input);
        System.out.println("Compressed size: " + formatSize(compressedSize));

        long startTime = System.nanoTime();
        Decompressor.decompress(input, output);
        long elapsed = System.nanoTime() - startTime;

        long originalSize = new File(output).length();
        System.out.println("Decompressed size: " + formatSize(originalSize));
        System.out.printf("Time: %.3f ms%n", elapsed / 1_000_000.0);
        System.out.println("Output: " + output);
    }

    private static void runDemo() throws IOException {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║         Huffman Compression Demo                    ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Sample text (" + SAMPLE_TEXT.length() + " characters):");
        System.out.println("\"" + SAMPLE_TEXT.substring(0, 80) + "...\"");
        System.out.println();

        byte[] data = SAMPLE_TEXT.getBytes("UTF-8");
        HuffmanTree tree = Compressor.getTreeForData(data);

        System.out.println("Huffman Code Table:");
        tree.printCodeTable();
        System.out.println();

        File tempInput = File.createTempFile("huffman_demo_", ".txt");
        File tempCompressed = File.createTempFile("huffman_demo_", ".huf");
        File tempDecompressed = File.createTempFile("huffman_demo_", ".txt");
        tempInput.deleteOnExit();
        tempCompressed.deleteOnExit();
        tempDecompressed.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempInput)) {
            fos.write(data);
        }

        long originalSize = tempInput.length();
        long startCompress = System.nanoTime();
        long compressedSize = Compressor.compress(tempInput.getAbsolutePath(), tempCompressed.getAbsolutePath());
        long compressTime = System.nanoTime() - startCompress;

        long startDecompress = System.nanoTime();
        Decompressor.decompress(tempCompressed.getAbsolutePath(), tempDecompressed.getAbsolutePath());
        long decompressTime = System.nanoTime() - startDecompress;

        byte[] decompressedData = new byte[(int) tempDecompressed.length()];
        try (FileInputStream fis = new FileInputStream(tempDecompressed)) {
            fis.read(decompressedData);
        }

        boolean match = Arrays.equals(data, decompressedData);

        System.out.println("Compression Statistics:");
        System.out.println("┌────────────────────────┬──────────────────┐");
        System.out.printf("│ Original size          │ %14s   │%n", formatSize(originalSize));
        System.out.printf("│ Compressed size        │ %14s   │%n", formatSize(compressedSize));
        System.out.printf("│ Compression ratio      │ %13.1f%%   │%n", (double) compressedSize / originalSize * 100);
        System.out.printf("│ Space savings          │ %13.1f%%   │%n", (1 - (double) compressedSize / originalSize) * 100);
        System.out.printf("│ Compress time          │ %11.3f ms   │%n", compressTime / 1_000_000.0);
        System.out.printf("│ Decompress time        │ %11.3f ms   │%n", decompressTime / 1_000_000.0);
        System.out.printf("│ Integrity check        │ %14s   │%n", match ? "PASSED" : "FAILED");
        System.out.println("└────────────────────────┴──────────────────┘");

        tempInput.delete();
        tempCompressed.delete();
        tempDecompressed.delete();
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }

    private static void printUsage() {
        System.out.println("Huffman Compression Tool");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java Main -c <input> <output>   Compress a file");
        System.out.println("  java Main -d <input> <output>   Decompress a file");
        System.out.println("  java Main --demo                Run demo with sample text");
    }
}
