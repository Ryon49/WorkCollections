CC=gcc
SDIR=src
DEPS=$(SDIR)/hw2.c $(SDIR)/helpers2.c 
BDIR=bin
INCLUDE=include

default: $(SDEPS)
	mkdir -p $(BDIR)
	$(CC) -o $(BDIR)/FMC $(DEPS) $(SDIR)/main.c -I $(INCLUDE)

debug: $(SDEPS)
	mkdir -p $(BDIR)
	$(CC) -g -o $(BDIR)/FMC $(DEPS) $(SDIR)/main.c -I $(INCLUDE)

clean:
	rm -rf $(BDIR)

test:
	sh test.sh