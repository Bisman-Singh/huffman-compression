JAVAC = javac
JAVA = java
SRC_DIR = src
OUT_DIR = out
SOURCES = $(wildcard $(SRC_DIR)/*.java)
CLASSES = $(patsubst $(SRC_DIR)/%.java, $(OUT_DIR)/%.class, $(SOURCES))

.PHONY: build run clean

build: $(OUT_DIR)
	$(JAVAC) --release 17 -d $(OUT_DIR) $(SOURCES)

$(OUT_DIR):
	mkdir -p $(OUT_DIR)

run: build
	$(JAVA) -cp $(OUT_DIR) Main --demo

clean:
	rm -rf $(OUT_DIR)
