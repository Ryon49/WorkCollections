CC=gcc
SDIR=src
DEPS=$(SDIR)/hw1.c $(SDIR)/helpers1.c 
BDIR=bin
INCLUDE=include

default: $(SDEPS)
	mkdir -p $(BDIR)
	$(CC) -o $(BDIR)/53wc $(DEPS) -I $(INCLUDE)

clean:
	rm -rf $(BDIR)


