make

rm -rf output
mkdir output
# default key
./bin/FMC -e rsrc/theBIGC.txt | ./bin/FMC -O output/theBIGC.txt -d
./bin/FMC -e rsrc/symbols.txt | ./bin/FMC -O output/symbols.txt -d
./bin/FMC -e rsrc/other_symbols.txt | ./bin/FMC -O output/other_symbols.txt -d
./bin/FMC -e rsrc/numbers.txt | ./bin/FMC -O output/numbers.txt -d
./bin/FMC -e rsrc/num_letters.txt | ./bin/FMC -O output/num_letters.txt -d
./bin/FMC -e rsrc/nonASCIIchar.txt | ./bin/FMC -O output/nonASCIIchar.txt -d
./bin/FMC -e rsrc/marypoppins.txt | ./bin/FMC -O output/marypoppins.txt -d
./bin/FMC -e rsrc/emptyfile.txt | ./bin/FMC -O output/emptyfile.txt -d
./bin/FMC -e rsrc/bugsInCode.txt | ./bin/FMC -O output/bugsInCode.txt -d
./bin/FMC -e rsrc/startspace.txt | ./bin/FMC -O output/startspace.txt -d
./bin/FMC -e rsrc/endspace.txt | ./bin/FMC -O output/endspace.txt -d
./bin/FMC -e rsrc/manyspace.txt | ./bin/FMC -O output/manyspace.txt -d
# ./bin/FMC -e rsrc/anteatersGO.txt | ./bin/FMC -O output/anteatersGO.txt -d

diff output/theBIGC.txt rsrc/decoded/THEBIGC.txt 
diff output/symbols.txt rsrc/decoded/SYMBOLS.txt 
diff output/other_symbols.txt rsrc/decoded/OTHER_SYMBOLS.txt 
diff output/numbers.txt rsrc/decoded/NUMBERS.txt 
diff output/num_letters.txt rsrc/decoded/NUM_LETTERS.txt 
diff output/nonASCIIchar.txt rsrc/decoded/NONASCIICHAR.txt 
diff output/marypoppins.txt rsrc/decoded/MARYPOPPINS.txt 
diff output/emptyfile.txt rsrc/decoded/EMPTYFILE.txt 
diff output/bugsInCode.txt rsrc/decoded/BUGSINCODE.txt 
diff output/startspace.txt rsrc/decoded/STARTSPACE.txt
diff output/endspace.txt rsrc/decoded/ENDSPACE.txt
diff output/manyspace.txt rsrc/decoded/MANYSPACE.txt
# diff output/anteatersGO.txt rsrc/decoded/ANTEATERSGO.txt 

# custom key ics53
./bin/FMC -k ics53 -e rsrc/theBIGC.txt | ./bin/FMC -k ics53 -O output/theBIGC.txt -d
./bin/FMC -k ics53 -e rsrc/symbols.txt | ./bin/FMC -k ics53 -O output/symbols.txt -d
./bin/FMC -k ics53 -e rsrc/other_symbols.txt | ./bin/FMC -k ics53 -O output/other_symbols.txt -d
./bin/FMC -k ics53 -e rsrc/numbers.txt | ./bin/FMC -k ics53 -O output/numbers.txt -d
./bin/FMC -k ics53 -e rsrc/num_letters.txt | ./bin/FMC -k ics53 -O output/num_letters.txt -d
./bin/FMC -k ics53 -e rsrc/nonASCIIchar.txt | ./bin/FMC -k ics53 -O output/nonASCIIchar.txt -d
./bin/FMC -k ics53 -e rsrc/marypoppins.txt | ./bin/FMC -k ics53 -O output/marypoppins.txt -d
./bin/FMC -k ics53 -e rsrc/emptyfile.txt | ./bin/FMC -k ics53 -O output/emptyfile.txt -d
./bin/FMC -k ics53 -e rsrc/startspace.txt | ./bin/FMC -k ics53 -O output/startspace.txt -d
./bin/FMC -k ics53 -e rsrc/endspace.txt | ./bin/FMC -k ics53 -O output/endspace.txt -d
./bin/FMC -k ics53 -e rsrc/manyspace.txt | ./bin/FMC -k ics53 -O output/manyspace.txt -d
# ./bin/FMC -k ics53 -e rsrc/bugsInCode.txt | ./bin/FMC -k ics53 make-O output/bugsInCode.txt -d

diff output/theBIGC.txt rsrc/decoded/THEBIGC.txt 
diff output/symbols.txt rsrc/decoded/SYMBOLS.txt 
diff output/other_symbols.txt rsrc/decoded/OTHER_SYMBOLS.txt 
diff output/numbers.txt rsrc/decoded/NUMBERS.txt 
diff output/num_letters.txt rsrc/decoded/NUM_LETTERS.txt 
diff output/nonASCIIchar.txt rsrc/decoded/NONASCIICHAR.txt 
diff output/marypoppins.txt rsrc/decoded/MARYPOPPINS.txt 
diff output/emptyfile.txt rsrc/decoded/EMPTYFILE.txt 
diff output/bugsInCode.txt rsrc/decoded/BUGSINCODE.txt
diff output/startspace.txt rsrc/decoded/STARTSPACE.txt
diff output/endspace.txt rsrc/decoded/ENDSPACE.txt
diff output/manyspace.txt rsrc/decoded/MANYSPACE.txt
# diff output/anteatersGO.txt rsrc/decoded/ANTEATERSGO.txt 


# custom key
./bin/FMC -k uciicsopenlab -e rsrc/theBIGC.txt | ./bin/FMC -k uciicsopenlab -O output/theBIGC.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/symbols.txt | ./bin/FMC -k uciicsopenlab -O output/symbols.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/other_symbols.txt | ./bin/FMC -k uciicsopenlab -O output/other_symbols.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/numbers.txt | ./bin/FMC -k uciicsopenlab -O output/numbers.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/num_letters.txt | ./bin/FMC -k uciicsopenlab -O output/num_letters.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/nonASCIIchar.txt | ./bin/FMC -k uciicsopenlab -O output/nonASCIIchar.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/marypoppins.txt | ./bin/FMC -k uciicsopenlab -O output/marypoppins.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/emptyfile.txt | ./bin/FMC -k uciicsopenlab -O output/emptyfile.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/startspace.txt | ./bin/FMC -k uciicsopenlab -O output/startspace.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/endspace.txt | ./bin/FMC -k uciicsopenlab -O output/endspace.txt -d
./bin/FMC -k uciicsopenlab -e rsrc/manyspace.txt | ./bin/FMC -k uciicsopenlab -O output/manyspace.txt -d
# ./bin/FMC -k uciicsopenlab -e rsrc/bugsInCode.txt | ./bin/FMC -k uciicsopenlab make-O output/bugsInCode.txt -d

diff output/theBIGC.txt rsrc/decoded/THEBIGC.txt 
diff output/symbols.txt rsrc/decoded/SYMBOLS.txt 
diff output/other_symbols.txt rsrc/decoded/OTHER_SYMBOLS.txt 
diff output/numbers.txt rsrc/decoded/NUMBERS.txt 
diff output/num_letters.txt rsrc/decoded/NUM_LETTERS.txt 
diff output/nonASCIIchar.txt rsrc/decoded/NONASCIICHAR.txt 
diff output/marypoppins.txt rsrc/decoded/MARYPOPPINS.txt 
diff output/emptyfile.txt rsrc/decoded/EMPTYFILE.txt 
diff output/bugsInCode.txt rsrc/decoded/BUGSINCODE.txt 
diff output/startspace.txt rsrc/decoded/STARTSPACE.txt
diff output/endspace.txt rsrc/decoded/ENDSPACE.txt
diff output/manyspace.txt rsrc/decoded/MANYSPACE.txt
# diff output/anteatersGO.txt rsrc/decoded/ANTEATERSGO.txt 


# custom key "I'LL be back!"
./bin/FMC -k "I'LL be back\!" -e rsrc/theBIGC.txt | ./bin/FMC -k "I'LL be back\!" -O output/theBIGC.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/symbols.txt | ./bin/FMC -k "I'LL be back\!" -O output/symbols.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/other_symbols.txt | ./bin/FMC -k "I'LL be back\!" -O output/other_symbols.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/numbers.txt | ./bin/FMC -k "I'LL be back\!" -O output/numbers.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/num_letters.txt | ./bin/FMC -k "I'LL be back\!" -O output/num_letters.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/nonASCIIchar.txt | ./bin/FMC -k "I'LL be back\!" -O output/nonASCIIchar.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/marypoppins.txt | ./bin/FMC -k "I'LL be back\!" -O output/marypoppins.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/emptyfile.txt | ./bin/FMC -k "I'LL be back\!" -O output/emptyfile.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/startspace.txt | ./bin/FMC -k "I'LL be back\!" -O output/startspace.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/endspace.txt | ./bin/FMC -k "I'LL be back\!" -O output/endspace.txt -d
./bin/FMC -k "I'LL be back\!" -e rsrc/manyspace.txt | ./bin/FMC -k "I'LL be back\!" -O output/manyspace.txt -d
# ./bin/FMC -k "I'LL be back\!" -e rsrc/bugsInCode.txt | ./bin/FMC -k "I'LL be back\!" make-O output/bugsInCode.txt -d

diff output/theBIGC.txt rsrc/decoded/THEBIGC.txt 
diff output/symbols.txt rsrc/decoded/SYMBOLS.txt 
diff output/other_symbols.txt rsrc/decoded/OTHER_SYMBOLS.txt 
diff output/numbers.txt rsrc/decoded/NUMBERS.txt 
diff output/num_letters.txt rsrc/decoded/NUM_LETTERS.txt 
diff output/nonASCIIchar.txt rsrc/decoded/NONASCIICHAR.txt 
diff output/marypoppins.txt rsrc/decoded/MARYPOPPINS.txt 
diff output/emptyfile.txt rsrc/decoded/EMPTYFILE.txt 
diff output/bugsInCode.txt rsrc/decoded/BUGSINCODE.txt
diff output/startspace.txt rsrc/decoded/STARTSPACE.txt
diff output/endspace.txt rsrc/decoded/ENDSPACE.txt
diff output/manyspace.txt rsrc/decoded/MANYSPACE.txt
# diff output/anteatersGO.txt rsrc/decoded/ANTEATERSGO.txt 

rm -rf output
make clean