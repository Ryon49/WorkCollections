#include "hw3_p3.h"

IPV4_validation validateIPV4List(List_t* packets) {
    IPV4_validation valid;
    valid.packet = NULL;
    valid.errcode = NOERR;

    node_t *ref = packets->head;
    while (ref != NULL) {
        IPV4_node *node = (IPV4_node *) ref->value;
        IPV4_header *header = (IPV4_header *) node->sop;

        node->fragment_offset = header->fragment_offset;
        node->payload = (char *)(((char *)header) + header->header_length * 4);
        node->payload_len = header->total_length - header->header_length * 4;

        if (verifyIPV4Checksum(header) != 0) {
            valid.packet = node;
            valid.errcode = ERRSUM;
            break;
        }
        if (ref->next != NULL) {
            IPV4_node *nextNode = (IPV4_node *) ref->next->value;
            IPV4_header *nextHeader = (IPV4_header *) nextNode->sop;

            if ((node->fragment_offset + node->payload_len) != nextHeader->fragment_offset) {
                valid.packet = node;
                valid.errcode = ERRHOLE;
                break;
            }
        }

        ref = ref->next;
    }
    return valid;
}