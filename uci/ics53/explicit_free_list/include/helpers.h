#ifndef HELPERS_H
#define HELPERS_H

#include "icsmm.h"
#include <stdio.h>

/* Helper function declarations go here */
size_t alignSize(size_t size);

bool fit(ics_header header, size_t size);

ics_free_header *find_fit(size_t size);

ics_free_header *find_end();

#endif
