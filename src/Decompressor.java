import java.io.*;

public class Decompressor {

    private static final byte[] MAGIC = { 'H', 'U', 'F', 'F' };

    public static void decompress(String inputPath, String outputPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputPath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] magic = new byte[4];
            if (bis.read(magic) != 4) {
                throw new IOException("Invalid file: too short");
            }
            for (int i = 0; i < 4; i++) {
                if (magic[i] != MAGIC[i]) {
                    throw new IOException("Invalid file: bad magic number");
                }
            }

            DataInputStream dis = new DataInputStream(bis);
            int headerLen = dis.readInt();
            byte[] headerBytes = new byte[headerLen];
            dis.readFully(headerBytes);

            DataInputStream headerIn = new DataInputStream(new ByteArrayInputStream(headerBytes));
            long originalSize = headerIn.readLong();
            int uniqueBytes = headerIn.readInt();

            int[] frequencies = new int[256];
            for (int i = 0; i < uniqueBytes; i++) {
                int b = headerIn.readUnsignedByte();
                int freq = headerIn.readInt();
                frequencies[b] = freq;
            }

            if (originalSize == 0) {
                new FileOutputStream(outputPath).close();
                return;
            }

            HuffmanTree tree = new HuffmanTree();
            tree.buildTree(frequencies);
            HuffmanNode root = tree.getRoot();

            int padding = dis.readUnsignedByte();

            byte[] compressedData = dis.readAllBytes();

            try (FileOutputStream fos = new FileOutputStream(outputPath);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {

                BitReader bitReader = new BitReader(new ByteArrayInputStream(compressedData));
                long totalBits = (long) compressedData.length * 8 - padding;
                long bitsRead = 0;
                long bytesWritten = 0;

                HuffmanNode current = root;
                while (bytesWritten < originalSize && bitsRead < totalBits) {
                    int bit = bitReader.readBit();
                    if (bit == -1) break;
                    bitsRead++;

                    if (bit == 0) {
                        current = current.left;
                    } else {
                        current = current.right;
                    }

                    if (current != null && current.isLeaf()) {
                        bos.write(current.byteValue);
                        bytesWritten++;
                        current = root;
                    }
                }
            }
        }
    }
}
