#include "hw3_p3.h"

// part 3 main
int main(int argc, char* argv[]){

	List_t* packetList = buildIPV4List(argv[1]);	
	
	IPV4_validation err = validateIPV4List(packetList);

	printIPV4List(packetList,err); 
	
	return 0;
}
