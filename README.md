# Huffman Compression

A file compression tool using Huffman coding. Supports any file type (text, binary) and provides lossless compression and decompression.

## How It Works

Huffman coding assigns variable-length binary codes to bytes based on their frequency — more frequent bytes get shorter codes. The algorithm builds a binary tree where each leaf represents a byte value, and the path from root to leaf defines the code.

## Building

```bash
make build
```

## Usage

```bash
# Compress a file
make run ARGS="-c input.txt output.huf"

# Decompress a file
make run ARGS="-d output.huf restored.txt"

# Run demo mode
make run
```

Or directly:

```bash
java -cp out Main -c myfile.txt myfile.huf
java -cp out Main -d myfile.huf myfile.txt
java -cp out Main --demo
```

## File Format

Compressed files use the `.huf` extension and contain:
1. Magic bytes (`HUFF`)
2. Header with frequency table and original file size
3. Padding info
4. Huffman-encoded data

## Clean

```bash
make clean
```

<sub><sup>Originally developed and tested locally during learning. Later organized and pushed to GitHub for portfolio visibility.</sup></sub>
