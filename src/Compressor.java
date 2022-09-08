import java.io.*;
import java.util.*;

public class Compressor {

    private static final byte[] MAGIC = { 'H', 'U', 'F', 'F' };

    public static long compress(String inputPath, String outputPath) throws IOException {
        byte[] data = readAllBytes(inputPath);

        int[] frequencies = new int[256];
        for (byte b : data) {
            frequencies[b & 0xFF]++;
        }

        HuffmanTree tree = new HuffmanTree();
        tree.buildTree(frequencies);
        Map<Integer, String> codes = tree.getCodeTable();

        try (FileOutputStream fos = new FileOutputStream(outputPath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            bos.write(MAGIC);

            ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
            DataOutputStream headerOut = new DataOutputStream(headerBuf);
            headerOut.writeLong(data.length);

            int uniqueBytes = 0;
            for (int f : frequencies) {
                if (f > 0) uniqueBytes++;
            }
            headerOut.writeInt(uniqueBytes);

            for (int i = 0; i < 256; i++) {
                if (frequencies[i] > 0) {
                    headerOut.writeByte(i);
                    headerOut.writeInt(frequencies[i]);
                }
            }
            headerOut.flush();
            byte[] headerBytes = headerBuf.toByteArray();

            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(headerBytes.length);
            dos.write(headerBytes);

            if (data.length == 0) {
                dos.writeByte(0);
                dos.flush();
                return new File(outputPath).length();
            }

            ByteArrayOutputStream compressedBuf = new ByteArrayOutputStream();
            BitWriter bitWriter = new BitWriter(compressedBuf);

            for (byte b : data) {
                String code = codes.get(b & 0xFF);
                bitWriter.writeBits(code);
            }

            int padding = bitWriter.getPaddingBits();
            bitWriter.flush();

            dos.writeByte(padding);
            byte[] compressedBytes = compressedBuf.toByteArray();
            dos.write(compressedBytes);
            dos.flush();
        }

        return new File(outputPath).length();
    }

    public static Map<Integer, String> getCodesForData(byte[] data) {
        int[] frequencies = new int[256];
        for (byte b : data) {
            frequencies[b & 0xFF]++;
        }
        HuffmanTree tree = new HuffmanTree();
        tree.buildTree(frequencies);
        return tree.getCodeTable();
    }

    public static HuffmanTree getTreeForData(byte[] data) {
        int[] frequencies = new int[256];
        for (byte b : data) {
            frequencies[b & 0xFF]++;
        }
        HuffmanTree tree = new HuffmanTree();
        tree.buildTree(frequencies);
        return tree;
    }

    private static byte[] readAllBytes(String path) throws IOException {
        File file = new File(path);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int offset = 0;
            while (offset < data.length) {
                int read = fis.read(data, offset, data.length - offset);
                if (read == -1) break;
                offset += read;
            }
        }
        return data;
    }
}
