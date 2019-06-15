#include "hw3_p2.h"

// part 2 main
int main(int argc, char* argv[]){

	IPV4_header* header = buildIPV4Header(argv[1]);	
	
	uint16_t checksum = verifyIPV4Checksum(header);

	printf("Checksum for %s: %04x\n", argv[1], checksum);
	
	return 0;
}
