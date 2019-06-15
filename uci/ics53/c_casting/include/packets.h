#ifndef PACKETS_H
#define PACKETS_H

#include "linkedList.h"
#include <stdint.h>

typedef struct __attribute__((__packed__)) {
    uint16_t total_length;
    uint8_t  type_of_service;
    uint8_t  header_length : 4;
    uint8_t  version : 4;
    uint16_t fragment_offset : 13;
    uint8_t  flags : 3;
    uint16_t identifier;
    uint16_t checksum;
    uint8_t  protocol;
    uint8_t  TTL;        //Time to live
    uint32_t source_ip;
    uint32_t dest_ip;
    uint32_t optional[10]; //Optional fields may or may not be used
} IPV4_header;

typedef struct {
    void*       sop;            // start of packet
    uint16_t    fragment_offset; // length of payload
    char*       payload;        // start of payload
    int         payload_len;    // length of payload
} IPV4_node;

typedef struct {
    IPV4_node* packet;  // pointer to violating packet (see hw doc)
    int        errcode; // errcode set by validateIPV4List
} IPV4_validation;

#define NOERR   0
#define ERRSUM  1
#define ERRHOLE 2

/* This function will attempt to build a packet header from the file specified by 
 * filepath. If there is any error during this function, this function will crash.
 *
 * @return Pointer to the packet header 
 */
IPV4_header* buildIPV4Header(const char* filepath);

/* This function will attempt to build a packet list from the file specified by 
 * filepath. If there is any error during this function, this function will crash.
 *
 * @return Pointer to the packet list
 */
List_t* buildIPV4List(const char* filepath);

/* Compares the packet pointed to by packet1 to the packet pointed to by packet2. 
 * Packets are compared using the fragment_offset field in the header.
 * 
 * @return -1 if packet1 is less than packet2
 *          1 if packet1 is greater than packet2
 *          0 if packet1 is equal to packet2
 */
int IPV4Comparator(void* packet1, void* packet2);

/* This function will print the bytes of the reconstructed payload (in hex).
 * It uses the payload and payload_len fields to print the correct amount of bytes
 * from each packet in the list.
 * If validateIPV4List returns a IPV4_validation struct containing an error, the
 * appropriate error message is printed by this function.
 *
 */
void printIPV4List(List_t* packets, IPV4_validation valid);

#endif
