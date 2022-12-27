package simpledb.optimizer;

import simpledb.execution.Predicate;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

    /**
     * Stores count for value
     */
    private int[] buckets;

    /**
     * Number of bucket
     */
    private int numBuckets;

    /**
     * Minimum value avaiable
     */
    private int min;

    /**
     * Maximum value avaiable
     */
    private int max;

    /**
     * Number of values this histogram has recorded.
     */
    private int ntups;

    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int numBuckets, int min, int max) {
        this.buckets = new int[numBuckets];
        this.numBuckets = numBuckets;
        this.min = min;
        this.max = max;
        this.ntups = 0;
    }

    /**
     * map value into histogram id
     * @param value
     * @return
     */
    private int getBucketIndex(int value) {
        if (value == this.max) {
            return this.numBuckets - 1;
        }
        double width = (double)(this.max - this.min) / this.numBuckets;
        // System.out.printf("getBucketIndex: (%d - %d) / (%f)\n", value, this.min, width);
        return (int)((value - this.min) / width);
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
        this.ntups += 1;
        this.buckets[getBucketIndex(v)] += 1;
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
        switch (op) {
            case EQUALS:
                if (v < this.min || v > this.max) {
                    return 0;
                }
                int h = this.buckets[this.getBucketIndex(v)];
                return (double)(h) / this.ntups;
            case GREATER_THAN:
            case GREATER_THAN_OR_EQ:
                if (v < this.min) {
                    return 1;
                } else if (v > this.max) {
                    return 0;
                }
                double res = 0;
                for (int i = v + 1; i <= this.max; i++) {
                    res += estimateSelectivity(Predicate.Op.EQUALS, i);
                }
                if (op == Predicate.Op.GREATER_THAN_OR_EQ) {
                    res += estimateSelectivity(Predicate.Op.EQUALS, v);
                }
                return res;
            case LESS_THAN:
            case LESS_THAN_OR_EQ:
                if (v < this.min) {
                    return 0;
                } else if (v > this.max) {
                    return 1;
                }
                res = 0;
                for (int i = v - 1; i >= this.min; i--) {
                    res += estimateSelectivity(Predicate.Op.EQUALS, i);
                }
                if (op == Predicate.Op.LESS_THAN_OR_EQ) {
                    res += estimateSelectivity(Predicate.Op.EQUALS, v);
                }
                return res;
            case NOT_EQUALS:
                return 1 - estimateSelectivity(Predicate.Op.EQUALS, v);
            default:
                return -1;
        }
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return 1.0;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
        // some code goes here
        return null;
    }

    public int numTuples() {
        return this.ntups;
    }
}
