import java.io.*;

public class BitReader implements AutoCloseable {
    private InputStream in;
    private int currentByte;
    private int bitsRemaining;

    public BitReader(InputStream in) {
        this.in = in;
        this.currentByte = 0;
        this.bitsRemaining = 0;
    }

    public int readBit() throws IOException {
        if (bitsRemaining == 0) {
            currentByte = in.read();
            if (currentByte == -1) return -1;
            bitsRemaining = 8;
        }
        bitsRemaining--;
        return (currentByte >> bitsRemaining) & 1;
    }

    public int readByte() throws IOException {
        int value = 0;
        for (int i = 0; i < 8; i++) {
            int bit = readBit();
            if (bit == -1) return -1;
            value = (value << 1) | bit;
        }
        return value;
    }

    public int readInt() throws IOException {
        int value = 0;
        for (int i = 0; i < 32; i++) {
            int bit = readBit();
            if (bit == -1) return -1;
            value = (value << 1) | bit;
        }
        return value;
    }

    public long readLong() throws IOException {
        long value = 0;
        for (int i = 0; i < 64; i++) {
            int bit = readBit();
            if (bit == -1) return -1;
            value = (value << 1) | bit;
        }
        return value;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
