compile:
	@echo "cleaning directory.."
	@make clean
	@echo "compiling src.."
	@javac -classpath ".:jars/*" -Xlint:unchecked src/*.java 
	@echo "creating executable jar file.."
	@jar cvfm mine.jar manifest.txt src/*.class > /dev/null
	@mkdir bin
	@mv src/*.class bin
	@mv mine.jar bin
	@cp -r jars bin
	@echo 'complete.'

submission:
	@echo "cleaning..."; make clean
	@echo "recompiling..."; make compile
	@rm -f *.zip
	@echo ""
	@read -p "Enter Last Name: " lastName; \
	 echo ""; \
	 read -p "Enter Student ID Number: " idNumber; \
	 echo ""; \
	 read -p "Enter Team Name: " teamName; \
	 echo "zipping src/ and mine.jar"; \
	 zip -rqq $${lastName}_$${idNumber}_$${teamName}.zip src mine.jar
	@echo "Done."

clean:
	rm -rf bin *.zip
