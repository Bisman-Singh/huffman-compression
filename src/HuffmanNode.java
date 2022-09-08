import java.io.Serializable;

public class HuffmanNode implements Comparable<HuffmanNode>, Serializable {
    private static final long serialVersionUID = 1L;

    int byteValue;
    int frequency;
    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode(int byteValue, int frequency) {
        this.byteValue = byteValue;
        this.frequency = frequency;
    }

    public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
        this.byteValue = -1;
        this.frequency = frequency;
        this.left = left;
        this.right = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        int cmp = Integer.compare(this.frequency, other.frequency);
        if (cmp != 0) return cmp;
        return Integer.compare(this.byteValue, other.byteValue);
    }
}
