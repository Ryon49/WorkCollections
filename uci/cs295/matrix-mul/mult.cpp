#include <time.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <pthread.h>

extern "C" {
#include <immintrin.h>
}

int simd_size = 8;

void transpose(float* matrix, int matrix_size) {
	int r, c;
	float temp;
	for (r = 0; r < matrix_size; r++) {
		int r_index = r * matrix_size;
		for (c = r + 1; c < matrix_size; c++) {
			temp = matrix[r_index + c];
			matrix[r_index + c] = matrix[c * matrix_size + r];
			matrix[c * matrix_size + r] = temp;
		}
	}
}

typedef struct param {
	float *a;
	float *b;
	float *c;
	int matrix_size; 
	int i_bound_start;
	int i_bound_end;
} param_t;

void *thread_mult(void *ptr) {
	param_t *param = (param_t *)ptr;

	// extrack arguments
	float *a = param->a;
	float *b = param->b;
	float *c = param->c;
	int start = param->i_bound_start;
	int end = param->i_bound_end;
	int matrix_size = param->matrix_size;

	int simd_bound = matrix_size / simd_size * simd_size;

	for (int i = start, row_index = start * matrix_size; i < end; ++i, row_index += matrix_size) {
		for (int j = 0, col_index = 0; j < matrix_size; ++j, col_index += matrix_size) {
			int k;
			__m256 store = _mm256_setzero_ps();
			for (k = 0; k < simd_bound; k+=8) {
				store = _mm256_add_ps(
					_mm256_mul_ps(
						_mm256_load_ps(a + row_index + k), 
						_mm256_load_ps(b + col_index + k)), 
					store);
			}
			float sum = store[0] + store[1] + store[2] + store[3] + store[4] + store[5] + store[6] + store[7];

			// add the rest
			for (; k < matrix_size; ++k) {
				sum += a[row_index + k] * b[col_index + k];
			}
			c[row_index + j] = sum;
		}
	}
	return NULL;
}

void mult(float* a, float* b, float*c, int matrix_size, int thread_count) {
	transpose(b, matrix_size);

	pthread_t threads[thread_count];

	int segment_size = matrix_size / thread_count;
	for (int i = 0; i < thread_count; ++i) {
		param_t *param = (param_t *)malloc(sizeof(param_t));
		param->a = a;
		param->b = b;
		param->c = c;
		param->matrix_size = matrix_size;
		param->i_bound_start = i * segment_size;
		param->i_bound_end = i + 1 == thread_count ? matrix_size : (i + 1) * segment_size;
		pthread_create(&threads[i], NULL, thread_mult, param);
	}

	for (int i = 0; i < thread_count; ++i) {
		pthread_join(threads[i], NULL);
	}
}
