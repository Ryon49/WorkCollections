#include <time.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

#include <ctime>
#include <chrono>

void naive_mult(float* a, float* b, float* c, int matrix_size) {
	for ( int i = 0; i < matrix_size; i++ ) {
		for ( int j = 0; j < matrix_size; j++ ) {
			c[i*matrix_size+j] = 0;
			for ( int k = 0; k < matrix_size; k++ ) {
				c[i*matrix_size+j] += a[i*matrix_size+k]*b[k*matrix_size+j];
			}
		}
	}
}

extern void mult(float* a, float* b, float*c, int matrix_size, int thread_count);


int main(int argc, char** argv) {
	srand(time(NULL));
	int matrix_size = 512;
	int thread_count = 1;
	if ( argc >= 2 ) {
		thread_count = atoi(argv[1]);
	}
	if ( thread_count <= 0 || thread_count > 8 ) {
		thread_count = 1;
	}
	if ( argc >= 3 ) {
		matrix_size = atoi(argv[2]);
	}
	if ( matrix_size <= 0 || matrix_size > (1024*32) ) {
		matrix_size = 2048;
	}
	float* a = (float*)malloc(sizeof(float)*matrix_size*matrix_size);
	float* b = (float*)malloc(sizeof(float)*matrix_size*matrix_size);
	float* c = (float*)malloc(sizeof(float)*matrix_size*matrix_size);
	float* cg = (float*)malloc(sizeof(float)*matrix_size*matrix_size);
	for ( int i = 0; i < matrix_size*matrix_size; i++ ) {
		a[i] = (float)(rand()%100000)/1000.f;
		b[i] = (float)(rand()%100000)/1000.f;
		c[i] = 0;
		cg[i] = 0;
	}
	
	std::chrono::high_resolution_clock::time_point start;
	std::chrono::high_resolution_clock::time_point now;
	std::chrono::microseconds duration_micro;

	start = std::chrono::high_resolution_clock::now();
	naive_mult(a,b,cg,matrix_size);

	now = std::chrono::high_resolution_clock::now();
	duration_micro = std::chrono::duration_cast<std::chrono::microseconds> (now-start);
	printf( "Naive done : %f s\n", 0.000001f*duration_micro.count() );
	
	start = std::chrono::high_resolution_clock::now();

	mult(a,b,c,matrix_size,thread_count);

	now = std::chrono::high_resolution_clock::now();

	duration_micro = std::chrono::duration_cast<std::chrono::microseconds> (now-start);
	printf( "Optimized done : %f s\n", 0.000001f*duration_micro.count() );

	size_t incorrect_count = 0;
	for ( int i = 0; i < matrix_size*matrix_size; i++ ) {
		if ( c[i] != cg[i] ) {
			// printf("%f\n", cg[i] - c[i]);
			incorrect_count++;
		}
	}
	free(a);
	free(b);
	free(c);
	free(cg);
	if ( incorrect_count > 0 ) {
		printf( "Matrix results incorrect! %ld values different\n", incorrect_count );
	} else {
		printf( "Matrix results correct!\n" );
	}

}

