#include <float.h>
#include <math.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <png.h>
#include <x86intrin.h>
#include <omp.h>
#include "ced.h"
#include "student.h"
/*
    This file should contain all functions that are necessary to change to complete
    the project. Per the requirements given in the specification online you are
    welcome to change, add, or remove other code as well although this file is
    an attempt to organize the necessary for you. Please read this entire file before
    you begin working. You likely want to produce changes in the following order

    1. Improve the efficiency of the code by modifying structures and calls:

        - Are there inefficient structures?

        - Are there inefficient function calls?

        - Could code be reorganized?

    2. Perform loop unrolling:

        - Which loops should you unroll?

        - How might you need to rewrite the code to enable loop unrolling?

    3. Perform SIMD operations with Intel intrinsics:

        - Which functions is it worthwhile to do this for?

            Try parsing the code to see if you can tell what sizes look like.

        - Where is the logic simple enough to use intrinsics

    4. Use Openmp:

        -Where will it be most beneficial to parallelize?


    While it is definitely beneficial to understand the code it is not necessary
    to completely understand the details. Truthfully this may not even be possible
    as in some cases (such as the sigma value for the gaussian filter) the values
    passed in lack theoretical proof and are just shown to work well with test/should
    be determined after testing. Your goal should be to understand what each step is doing,
    meaning the inputs (where and when can it be called) and outputs and what those will be 
    needed for in the future.

    As a side note because different parameters will satisify different images better, the
    png lib has to convert the png to a grayscale image using default parameters, the code
    that this is adapted from is written for a different file type and makes some different
    assumptions (see the comments in ced.c for this original code) there is definite room
    for improvement in accuracy. See the details in the spec for exactly how improvements
    to the accuracy of the algorithms and send me an email njriasanovsky@berkeley.edu
    if there are any issues or questions (such as something more accurate being rejected
    as being incorrect.)
*/

/*
    This function interacts with ced.c to perform the canny_edge_detection algorithm
    on the src file.

    First it opens each of the files and exits if either is not able to be opened.

    Then it setups the read and determines how many rows the file has.

    This allows it allocate mem to perform the actual read, which is then
    executed.

    At this point the write will be setup and the actual algorithm begins.

    It consists of 4 steps:

    1. Apply a gaussian filter to the read data via a convolution to remove
    an initial noise.

    2. Calculate the intensity gradient using two known matrices and performing
    a convolution on them. This will be used to determine angles of pixels for
    future steps.

    3. Perform non-maximal supression on the pixels. This means that for each pixel
    determine the angle of its gradient: either 0, 45, 90, or 135. This is initially
    implemented in the previous step. If the pixel is the maximum of its neightbors
    in this direction then the pixel should remain, otherwise it should be supressed.

    4. Finalize the edges using hysteresis. This consists of using a minimum and a maximum,
    if the value of the pixel is larger than the maximum it is definitely an edge. If it is
    larger than the minimum it may be an edge but should only be considered as such if it
    neighbors an edge.

    Finally once these are complete the actual write will be performed.
*/

void canny_edge_detection(char* src, char* dst) {
	char header[8];
	png_structp png_read_ptr;
	png_infop read_info_ptr;
	png_infop read_end_ptr;
	png_structp png_write_ptr;
	png_infop write_info_ptr;

	//Open the source and destination file
	FILE *src_file = fopen(src, "rb");
	if (src_file == NULL) {
		fprintf(stderr, "Unable to open source file.\n");
		exit(1);
	}
	FILE *dst_file = fopen(dst, "wb");
	if (dst_file == NULL) {
		fprintf(stderr, "Unable to create destination file.\n");
		fclose(src_file);
		exit(1);
	}

	//Call library function to set up the information for reading
	setup_read(src_file, dst_file, &png_read_ptr, &read_info_ptr, &read_end_ptr);

	//Determines image features such as height and width
	setup_info(png_read_ptr, read_info_ptr);


	unsigned height = png_get_image_height(png_read_ptr, read_info_ptr);
	unsigned width = png_get_rowbytes(png_read_ptr, read_info_ptr);

	//Allocate memory to read the image data into
	png_bytep row_pointers[height];
	allocate_read_mem(png_read_ptr, row_pointers, height, width);

	//Execute the actual read
	execute_read(png_read_ptr, read_info_ptr, read_end_ptr, row_pointers);

	//Call library function to set up the information for writing
	setup_write(src_file, dst_file, png_read_ptr, read_info_ptr, read_end_ptr, &png_write_ptr, &write_info_ptr);   


	//Allocate memory to perform for the various steps of the algorithm
	png_bytep output_pointers[png_get_image_height(png_read_ptr, read_info_ptr)];
	png_bytep Gy_applied[png_get_image_height(png_read_ptr, read_info_ptr)];
	png_bytep Gx_applied[png_get_image_height(png_read_ptr, read_info_ptr)];
	png_bytep nms[png_get_image_height(png_read_ptr, read_info_ptr)];
	png_bytep final_output[png_get_image_height(png_read_ptr, read_info_ptr)];

	//Allocate the actual memory
	allocate_write_mem(png_write_ptr, output_pointers, Gy_applied, Gx_applied, nms, final_output, png_get_image_height(png_read_ptr, read_info_ptr), png_get_rowbytes(png_read_ptr, read_info_ptr));

	//Allocate enough space for intermediate arrays
	float *G = calloc(png_get_rowbytes(png_read_ptr, read_info_ptr) * png_get_image_height(png_read_ptr, read_info_ptr), sizeof(float));
	float *dir = calloc(png_get_rowbytes(png_read_ptr, read_info_ptr) * png_get_image_height(png_read_ptr, read_info_ptr), sizeof(float));


	//The four steps for the canny edge detection.
	gaussian_filter(row_pointers, output_pointers, png_get_rowbytes(png_read_ptr, read_info_ptr), png_get_image_height(png_read_ptr, read_info_ptr), 1.0);
	intensity_gradients(output_pointers, Gx_applied, Gy_applied, G, dir, png_get_rowbytes(png_read_ptr, read_info_ptr), png_get_image_height(png_read_ptr, read_info_ptr));
	non_maximum_suppression(nms, G, dir, png_get_rowbytes(png_read_ptr, read_info_ptr), png_get_image_height(png_read_ptr, read_info_ptr));
	hysteresis(final_output, nms, png_get_rowbytes(png_read_ptr, read_info_ptr), png_get_image_height(png_read_ptr, read_info_ptr), 105, 45);
	
	free(G);
	free(dir);
	
	//Complete the actual write
	execute_write(png_write_ptr, write_info_ptr, final_output);


	//Clear memory allocated for reading and writing
	cleanup_rows(png_write_ptr, output_pointers, Gy_applied, Gx_applied, nms, final_output, png_read_ptr, row_pointers, png_get_image_height(png_read_ptr, read_info_ptr));



	//Clear memory alloacted by the library
	cleanup_struct_mem(png_read_ptr, read_info_ptr, read_end_ptr, png_write_ptr, write_info_ptr);


	//Close out the files
	fclose(src_file);
	fclose(dst_file);
}

/*
    Compute a gaussian filter and then perform a convolution of it with the input pixels read from the file (which
    have previously been set to be grayscale.)

    Refer to wikipedia for how to generate the gaussian filter if you need to check any changes.

    https://en.wikipedia.org/wiki/Canny_edge_detector

    C comments can't do the formula format justice
*/
void gaussian_filter(png_bytep *input, png_bytep *output, const unsigned width, const unsigned height, const float sigma) {
	unsigned n;
	if (sigma < 0.5) {
		n = 3;
	} else if (sigma < 1.0) {
		n = 5;
	} else if (sigma < 1.5) {
		n = 7;
	} else if (sigma < 2.0) {
		n = 9;
	} else if (sigma < 2.5) {
		n = 11;
	} else {
		n = 13;
	}

	const float sigma_square = sigma * sigma;
	const float k = (n - 1) / 2.0 + 1;
	const float d = 2 * M_PI * sigma_square;
	float a = sigma_square * -0.5;

	float kernel[n * n];
	
	__m128 v_sigma = _mm_set1_ps(sigma_square);
	__m128 v_a = _mm_set1_ps(a);
	__m128 v_k = _mm_set1_ps(k);
	size_t float_size = sizeof(float);

	int split = n/16*16;

	#pragma omp parallel
	{
		#pragma omp for
		for (unsigned r = 0; r < n; r++) {
		int rk = r - k;
		__m128 v_r = _mm_set1_ps(rk);	
			for (unsigned c = 0; c < split; c+=16) {
				float *out = (float *)malloc(4);	

				__m128 factor = _mm_set_ps(c, c+1, c+2, c+3);

				__m128 v_c = _mm_sub_ps(factor, v_k);
				v_c = _mm_mul_ps(v_c, v_c);
				__m128 temp = _mm_div_ps(_mm_add_ps(v_r, v_c), v_a);
				_mm_store_ps(out, temp);

				for (int i = 0; i < 4; i++) {
					kernel[c + (i+r)*n] = exp(out[3 - i]) / d;
				}
			}
			for (unsigned c = split; c < n; c++) {
				int ck = c - k;
				kernel[c + r*n] = exp((rk * rk + ck * ck) / a) / d;
			}		
	}

	}
	convolution(input, output, kernel, width, height, n, true);
}

/*
    Performs a convolution of the input and a specified kernel.
    If you are curious about what a convolution is, look at 
    https://en.wikipedia.org/wiki/Convolution
    but for our purposes we can think of it as a transformation
    on the input using the kernel.
*/
void convolution(png_bytep *input, png_bytep *output, float *kernel, const unsigned width, const unsigned height, const int z, const bool normalize) {
	float min = FLT_MAX, max = -FLT_MAX;
	
	int half = z / 2;
	int wh = width - half, hh = height - half;

	float *arr = calloc(width*height, sizeof(float));
	int index = 0;

	#pragma omp parallel for
    for (int n = half; n < hh; n++) {
    	for (int m = half; m < wh; m++) {
        	float pixel = 0.0;
            size_t c = 0;
			#pragma omp reduction(+ : pixel)
			for (int i = -half; i <= half; i++) {
				int mi = m - i, mi_div = mi / width, mi_rem = mi % width;
				for (int j = -half; j <= half; j++, c++) {
					pixel += input[(n - j) + mi_div][mi_rem] * kernel[c];
				}
			}
			output[n][m] = arr[index++] = pixel;
            if (pixel < min) {
            	min = pixel;
            }
            if (pixel > max) {
            	max = pixel;
            }
        }
    }
	if (normalize) {  
		index = 0;
		const float minMax = (max - min) / MAX_BRIGHTNESS;
		#pragma omp parallel for
		for (int n = half; n < hh; n++) {
			for (int m = half; m < wh; m++) {
				output[n][m] = (arr[index++] - min) / minMax;
			}
		}
	}
}

/*
    Takes two known matrices and performs convolutions on them with the output of the previous
    step (the input to this function). The Gradient G is calculated using the two convolutions
    and the angles can be calculated using the arctan of the two convultion results.
*/

void intensity_gradients(png_bytep *input, png_bytep *Gx_applied, png_bytep *Gy_applied, float *G, float *dir, const unsigned width, const unsigned height) {
	float Gx[] = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
	float Gy[] = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
	
	convolution(input, Gx_applied, Gx, width, height, 3, false);
	const float Eight_PI = M_PI / 8;
	#pragma omp parallel for
	for (int j = 1; j < height - 1; j++) {	
		int partial = width * j;
		for (int i = 1; i < width - 1; i++) { 
			int c = i + partial;

			float temp = Gx_applied[j][i];
			G[c] = hypot(temp, temp);
			dir[c] = fmod(atan2(temp, temp) + M_PI, M_PI) / Eight_PI; 
		}
	}
}

/*
    Takes the input G which consists of the gradient values and using the direction to determine
    the direction of the gradient. Then checks if in the direction of the gradient (given by
    dir) it is a local maximum. If it is the value remains on, otherwise it is turned off.
*/

void non_maximum_suppression(png_bytep *nms, float *G, float *dir, const unsigned width, const unsigned height) {
	#pragma omp parallel for
	for (int j = 1; j < height - 1; j++) {
		for (int i = 1; i < width - 1; i++) { 	
			int c = i + width * j;

			// non_maximum_suppression
			int nn = c - width;
			int ss = c + width;

			float gc = G[c], dirc = dir[c];

			bool lessEqual1 = dirc <= 1, lessEqual3 = dirc <= 3, 
				lessEqual5 = dirc <= 5, lessEqual7 = dirc <= 7;
			
			if ((lessEqual1 || !lessEqual7) && gc > G[c - 1] && gc > G[c + 1]) {
				nms[j][i] = gc;
			} else if ((!lessEqual1 && lessEqual3) && gc > G[nn + 1] && gc > G[ss - 1]) {
				nms[j][i] = gc;
			} else if ((!lessEqual3 && lessEqual5) && gc > G[nn] && gc > G[ss]) {	
				nms[j][i] = gc;
			} else if ((!lessEqual5 && lessEqual7) && gc > G[nn - 1] && gc > G[ss + 1]) {
				nms[j][i] = gc;
			} else {
				nms[j][i] = 0;
			}
		}
	}
}

/*
     Takes the pixel values in nms and determines if the values are greater than tmax or tmin.
     If the value is greater than tmax then the brightness of the pixel is set to be maximal.
     If the value is greater than min then it will be turned on if any of its neighbors have
     been set to be edges. The output results are written to out.
*/
void hysteresis(png_bytep *out, png_bytep *nms, const unsigned width, const unsigned height, const unsigned tmax, const unsigned tmin) {
	int *edges = calloc(sizeof(int), width * height);

    int nbs[8], t, nbs_div, nbs_rem, nbsk;
	#pragma omp parallel		
	for (int j = 1; j < height - 1; j++) {	
		for (int i = 1; i < width - 1; i++) {
			if (nms[j][i] >= tmax && out[j][i] == 0) {
				out[j][i] = MAX_BRIGHTNESS;
				int nedges = 1;
				edges[0] = i + width * j;
				do {
					nedges--;
					t = edges[nedges];
 
					nbs[0] = t - width;     // nn
					nbs[4] = nbs[0] + 1;    // nw
					nbs[5] = nbs[0] - 1;    // ne

					nbs[1] = t + width;     // ss
					nbs[6] = nbs[1] + 1;    // sw
					nbs[7] = nbs[1] - 1;    // se

					nbs[2] = t + 1;         // ww
					nbs[3] = t - 1;         // ee
 
					for (int k = 0; k < 8; k++) {
						nbsk = nbs[k], nbs_div = nbsk / width, nbs_rem = nbsk % width;
						if (nms[nbs_div][nbs_rem] >= tmin && out[nbs_div][nbs_rem] == 0) {
							out[nbs_div][nbs_rem] = MAX_BRIGHTNESS;
							edges[nedges] = nbsk;
							nedges++;
						}
					}
				} while (nedges > 0);
			}
		}
	}
	free(edges);
}

/*
    Allocates the memory necessary to read from the png file. The png_ functions
    all interact with the PNG_LIB library and should be used as opposed
    to the traditional malloc. Note that a png_bytep * is used because to set the
    rows to contain the data a value of type png_bytep * must be presented.
    Also note a png_bytep is an unsigned char *
*/
void allocate_read_mem(png_structp png_read_ptr, png_bytep *row_pointers, unsigned height, unsigned width) {
	int split = height / 5 * 5;
	#pragma omp parallel 
	{
		#pragma omp for 
		for (unsigned row = 0; row < split; row+=5) {
			int row1 = row+1, row2 = row1+1, row3 = row2+1, row4 = row3+1;

			row_pointers[row] = png_malloc(png_read_ptr, width);
			memset(row_pointers[row], 0, width);
					
			row_pointers[row1] = png_malloc(png_read_ptr, width);
			memset(row_pointers[row1], 0, width);

			row_pointers[row2] = png_malloc(png_read_ptr, width);
			memset(row_pointers[row2], 0, width);

			row_pointers[row3] = png_malloc(png_read_ptr, width);
			memset(row_pointers[row3], 0, width);

			row_pointers[row4] = png_malloc(png_read_ptr, width);
			memset(row_pointers[row4], 0, width);
		}

		#pragma omp single 
		for (unsigned row = split; row < height; row++) {
			row_pointers[row] = png_malloc(png_read_ptr, width);
			memset(row_pointers[row], 0, width);
		}
	}

}


/*
    Allocates the memory necessary to perform all of the steps of the edge detection algorithm.
    The png_ functions all interact with the PNG_LIB library and should be used as opposed to 
    the traditional malloc. Note that a png_bytep * is used because on must be final type for 
    rows presented to be written must be a png_bytep *. Also note a png_bytep is an unsigned char *
*/
void allocate_write_mem(png_structp png_write_ptr, png_bytep *rows1, png_bytep *rows2, png_bytep *rows3, png_bytep *rows4, png_bytep *rows5, unsigned height, unsigned width) {
	int split = height/2*2;

	size_t size = sizeof(png_bytep);

	#pragma omp parallel 
	{
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			rows1[row] = calloc(width, size);
			rows1[row+1] = calloc(width, size);
		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			rows2[row] = calloc(width, size);
			rows2[row+1] = calloc(width, size);

		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			rows3[row] = calloc(width, size);
			rows3[row+1] = calloc(width, size);
		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			rows4[row] = calloc(width, size);
			rows4[row+1] = calloc(width, size);
		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			rows5[row] = calloc(width, size);
			rows5[row+1] = calloc(width, size);
		}
		#pragma omp single nowait
		if (height % 2 != 0) {
			int split = height * 2 / 2;
			rows1[split] = calloc(width, size);			
			rows2[split] = calloc(width, size);			
			rows3[split] = calloc(width, size);			
			rows4[split] = calloc(width, size);			
			rows5[split] = calloc(width, size);			
		}
	}
}

/*
    Frees all of the memory allocated to hold the png information. The png_
    functions are all functions that interact with the PNG_LIB library and should
    be used as opposed to the traditional free.
*/
void cleanup_rows(png_structp png_write_ptr, png_bytep *rows1, png_bytep *rows2, png_bytep *rows3, png_bytep *rows4, png_bytep *rows5, png_structp png_read_ptr, png_bytep *readrows, unsigned height) {
	#pragma omp parallel 
	{
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			png_free(png_write_ptr, rows1[row]);
			png_free(png_write_ptr, rows1[row+1]);
		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			png_free(png_write_ptr, rows2[row]);
			png_free(png_write_ptr, rows2[row+1]);

		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			png_free(png_write_ptr, rows3[row]);
			png_free(png_write_ptr, rows3[row+1]);
		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			png_free(png_write_ptr, rows4[row]);
			png_free(png_write_ptr, rows4[row+1]);
		}
		#pragma omp single nowait
		for (unsigned row = 0; row < height; row+=2) {
			png_free(png_write_ptr, rows5[row]);
			png_free(png_write_ptr, rows5[row+1]);
		}
	}
}


/*
    Function responsible for initiating the edge detection program on 1 or more png images.
    This function is the first location in which processing begins.
*/
void handle_batch(char **src_values, char **dst_values, unsigned count) {
	#pragma omp parallel 
	{
		#pragma omp for nowait
		for (int i = 0; i < count; i+=2) {
			int i1 = i + 1;
			canny_edge_detection(src_values[i], dst_values[i]);
			canny_edge_detection(src_values[i1], dst_values[i1]);
		}
		#pragma omp single nowait
		if (count % 2 != 0) {
			int split = count * 2 / 2;
			canny_edge_detection(src_values[split], dst_values[split]);
		}
	}
}

