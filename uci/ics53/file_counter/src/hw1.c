// Weifeng Dong
// weifengd

#include <stdlib.h>

#include "hw1.h"

// main program
int main (int argc, char *argv[])
{
	if (argc < 2) {
		exit(1);
	}

	// check if there is a 2> stderr.txt
	bool do_redirect_stderr = false;

	bool do_symbol_count = false;
	bool do_number_count = false;
	bool do_whitespace_count = false;
	bool do_tab_count = false;
	bool do_space_count = false;

	// pre-process
	int i;
	for (i = 1; i < argc; i++) {
		char *option = argv[i];
		if (option[0] == '-') {
			if (option[1] == 'S') {
				do_symbol_count = true;
			} else if (option[1] == 'O') {
				do_redirect_stderr = true;
			} else if (option[1] == 'N') {
				do_number_count = true;
			} else if (option[1] == 'L') {
				do_whitespace_count = true;
			} else if (option[1] == 'E') {
				// skip the NUM argument
				i++;
				do_tab_count = true;
			} else if (option[1] == 'C') {
				// skip the NUM argument
				i++;
				do_space_count = true;
			} else {
				// Bad argument
				exit(1);
			}
		} else {
			// Bad argument
			exit(1);
		}
	}

	// count how many command were entered, "53wc -S -L" should be invalid
	int commandCount = 0;
	if (do_symbol_count) {
		commandCount++;
	}
	if (do_number_count) {
		commandCount++;
	}
	if (do_whitespace_count) {
		commandCount++;
	}
	if (do_tab_count) {
		commandCount++;
	}
	if (do_space_count) {
		commandCount++;
	}
	if (commandCount == 0 || commandCount >= 2) {
		// no command found, exit(1)
		// multiple command were enter, exit(1)
		exit(1);
	}

	bool hasError = false;
	bool hasContent = false;

	// command execution
	char c;
	int count = 0;
	if (do_symbol_count) {
		// countn symbols
		while (!hasError && (c = getc(stdin)) != EOF) {
			hasContent = true;
			// check if c is a ascii character
			if (!isascii(c)) {
				hasError = true;
			} else {
				if (isSymbol(c)) {
					count++;
				} else {
					print_char(c, do_redirect_stderr);
				}
			}
		}
	} else if (do_number_count) {
		bool numberFound = false;

		// count number
		while (!hasError && (c = getc(stdin)) != EOF) {
			hasContent = true;
			if (!isascii(c)) {
				hasError = true;
			} else {
				if (isNumber(c)) {
					// c is a number
					numberFound = true;
				} else {
					// only increase number_count when previous char is number and this char is not
					if (numberFound) {
						count++;
						numberFound = false;
					}
					print_char(c, do_redirect_stderr);
				}
			}
		}
	} else if (do_whitespace_count) {
		bool newLine = true;
		// iterate line
		while (!hasError && (c = getc(stdin)) != EOF) {
			hasContent = true;
			if (!isascii(c)) {
				hasError = true;
			} else {
				// leading whitespaces
				while (isWhitespace(c)) {
					if (newLine) {
						count++;
						newLine = false;
					}
					c = getc(stdin);
				}
				// the rest of line
				while (!hasError) {
					if (!isascii(c)) {
						hasError = true;
					} else {
						print_char(c, do_redirect_stderr);

						// reach end of line, reset, go to outer while loop
						if (c == '\n') {
							newLine = true;
							break;
						}
						c = getc(stdin);
					}
				}
			}
		}
	} else if (do_tab_count) {
		if (argc < 3) {
			// Not enough argument
			exit(1);
		}

		int replace_at = atoi(argv[2]);
		if (!compareValue(replace_at, argv[2])) {
			// NUM argument is invalid
			exit(1);
		}

		// count tabs
		while (!hasError && (c = getc(stdin)) != EOF) {
			hasContent = true;
			if (!isascii(c)) {
				hasError = true;
			} else {
				if (c == '\t') {
					count++;
					print_space(replace_at);
				} else {
					print_char(c, true);
				}
			}
		}
	} else if (do_space_count) {
		if (argc < 3) {
			// Not enough argument
			exit(1);
		}
		int replace_at = atoi(argv[2]);
		if (!compareValue(replace_at, argv[2])) {
			// NUM argument is invalid
			exit(1);
		}

		// count spaces
		int consecutive_space_count = 0;
		while (!hasError && (c = getc(stdin)) != EOF) {
			hasContent = true;
			if (!isascii(c)) {
				hasError = true;
			} else {
				if (c == ' ') {
					consecutive_space_count++;
					if (consecutive_space_count == replace_at) {
						print_char('\t', true);
						count++;
						consecutive_space_count = 0;
					}
				} else {
					print_space(consecutive_space_count);
					consecutive_space_count = 0;
					print_char(c, true);
				}
			}
		}
	}

	if (!hasContent) {
		exit(1);
	}
	fprintf(stdout, "%d\n", count);
	if (hasError) {
		exit(1);
	} else {
		return 0;
	}
}
