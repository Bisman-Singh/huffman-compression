import java.io.*;

public class BitWriter implements AutoCloseable {
    private OutputStream out;
    private int currentByte;
    private int bitCount;
    private long totalBitsWritten;

    public BitWriter(OutputStream out) {
        this.out = out;
        this.currentByte = 0;
        this.bitCount = 0;
        this.totalBitsWritten = 0;
    }

    public void writeBit(int bit) throws IOException {
        currentByte = (currentByte << 1) | (bit & 1);
        bitCount++;
        totalBitsWritten++;

        if (bitCount == 8) {
            out.write(currentByte);
            currentByte = 0;
            bitCount = 0;
        }
    }

    public void writeBits(String bits) throws IOException {
        for (char c : bits.toCharArray()) {
            writeBit(c == '1' ? 1 : 0);
        }
    }

    public void writeByte(int b) throws IOException {
        for (int i = 7; i >= 0; i--) {
            writeBit((b >> i) & 1);
        }
    }

    public void writeInt(int value) throws IOException {
        for (int i = 31; i >= 0; i--) {
            writeBit((value >> i) & 1);
        }
    }

    public void writeLong(long value) throws IOException {
        for (int i = 63; i >= 0; i--) {
            writeBit((int) ((value >> i) & 1));
        }
    }

    public void flush() throws IOException {
        if (bitCount > 0) {
            currentByte <<= (8 - bitCount);
            out.write(currentByte);
            currentByte = 0;
            bitCount = 0;
        }
        out.flush();
    }

    public int getPaddingBits() {
        return bitCount == 0 ? 0 : 8 - bitCount;
    }

    public long getTotalBitsWritten() {
        return totalBitsWritten;
    }

    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }
}
