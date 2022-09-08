import java.util.*;

public class HuffmanTree {
    private HuffmanNode root;
    private Map<Integer, String> codeTable;

    public HuffmanTree() {
        this.codeTable = new HashMap<>();
    }

    public HuffmanNode buildTree(int[] frequencies) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                pq.add(new HuffmanNode(i, frequencies[i]));
            }
        }

        if (pq.isEmpty()) {
            root = null;
            return null;
        }

        if (pq.size() == 1) {
            HuffmanNode only = pq.poll();
            root = new HuffmanNode(only.frequency, only, null);
            generateCodes(root, "");
            return root;
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode(left.frequency + right.frequency, left, right);
            pq.add(parent);
        }

        root = pq.poll();
        codeTable.clear();
        generateCodes(root, "");
        return root;
    }

    private void generateCodes(HuffmanNode node, String code) {
        if (node == null) return;

        if (node.isLeaf()) {
            codeTable.put(node.byteValue, code.isEmpty() ? "0" : code);
            return;
        }

        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    public Map<Integer, String> getCodeTable() {
        return Collections.unmodifiableMap(codeTable);
    }

    public HuffmanNode getRoot() {
        return root;
    }

    public void printCodeTable() {
        List<Map.Entry<Integer, String>> entries = new ArrayList<>(codeTable.entrySet());
        entries.sort(Comparator.comparingInt(e -> e.getValue().length()));

        System.out.println("┌──────────┬──────────┬────────────────────────────────┐");
        System.out.println("│  Byte    │  Char    │  Huffman Code                  │");
        System.out.println("├──────────┼──────────┼────────────────────────────────┤");

        for (Map.Entry<Integer, String> entry : entries) {
            int b = entry.getKey();
            String code = entry.getValue();
            String charRepr;
            if (b >= 32 && b < 127) {
                charRepr = String.valueOf((char) b);
            } else if (b == 10) {
                charRepr = "\\n";
            } else if (b == 13) {
                charRepr = "\\r";
            } else if (b == 9) {
                charRepr = "\\t";
            } else {
                charRepr = String.format("0x%02X", b);
            }
            System.out.printf("│  %-7d │  %-7s │  %-29s │%n", b, charRepr, code);
        }

        System.out.println("└──────────┴──────────┴────────────────────────────────┘");
    }
}
