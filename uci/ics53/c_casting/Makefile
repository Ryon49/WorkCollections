CC := gcc

LIB := $(shell find lib -type f -name *.o)
INC := -I include

DFLAGS := -g -DDEBUG -DCOLOR
CFLAGS := $(INC) -Wno-pointer-to-int-cast -Wno-int-to-pointer-cast
# CFLAGS := $(INC) -Wno-pointer-to-int-cast -Wno-int-to-pointer-cast $(DFLAGS)

.PHONY: clean all setup

all: 53csv lltest checksum packetlist

53csv: setup
	$(CC) $(CFLAGS) $(LIB) src/helpers.c src/hw3_p1.c src/53csv_main.c -o bin/53csv $(DFLAGS)

lltest: setup
	$(CC) $(CFLAGS) $(LIB) src/helpers.c src/hw3_p1.c tests/lltest_main.c -o bin/lltest

checksum: setup
	$(CC) $(CFLAGS) $(LIB) src/helpers.c src/hw3_p2.c tests/checksum_main.c -o bin/checksum

packetlist: setup
	$(CC) $(CFLAGS) $(LIB) src/helpers.c src/hw3_p2.c src/hw3_p3.c tests/packetlist_main.c -o bin/packetlist

setup:
	mkdir -p bin

clean:
	rm -rf .vscode
	$(RM) -r bin

# test:
# 	bin/53csv 1 < rsrc/part1/sample.csv > output/1.csv
# 	bin/53csv 2 < rsrc/part1/sample.csv > output/2.csv
# 	bin/53csv 3 < rsrc/part1/sample.csv > output/3.csv
# 	bin/53csv 4 < rsrc/part1/sample.csv > output/4.csv
# 	bin/53csv 5 < rsrc/part1/sample.csv > output/5.csv
# 	diff expect/1.csv output/1.csv
# 	diff expect/2.csv output/2.csv
# 	diff expect/3.csv output/3.csv
# 	diff expect/4.csv output/4.csv