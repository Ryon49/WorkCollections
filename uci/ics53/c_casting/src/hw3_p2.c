#include "hw3_p2.h"

uint16_t verifyIPV4Checksum(IPV4_header* header) {
    uint32_t sum = 0;

    uint16_t* cast = (uint16_t *)header;
    int i;
    for (i = 0; i < header->header_length * 2; i++) {
        sum += *(cast + i);
    }
    if (sum > 0xFFFF) {
        sum = ((sum & 0xffff0000) >> 16) + (sum & 0xffff);
    }

    return (sum & 0xFFFF) == 0xFFFF ? 0 : ~(sum & 0xFFFF);
}

