package simpledb.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import simpledb.common.Type;
import simpledb.execution.aggregator.*;
import simpledb.storage.Field;
import simpledb.storage.Tuple;
import simpledb.storage.TupleDesc;
import simpledb.storage.TupleIterator;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    /**
     * index of field used for grouping
     */
    private int gbfield;

    /**
     * type of field used for grouping
     */
    private Type gbfieldtype;

    /**
     * index of field used for aggregation
     */
    private int afield;

    /**
     * Aggregation operator
     */
    private Op what;

    /**
     * Used when NO_GROUPING is used
     */
    private CustomAggregator singleAggregator;

    /**
     * Manage each groupby state if using gruops
     */
    private HashMap<Field, CustomAggregator> groups;

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.what = what;
        this.singleAggregator = newAggregator();
        this.groups = new HashMap<>();
    }

    /**
     * @return a new custom aggregator for a new group
     */
    private CustomAggregator newAggregator() {
        if (this.what == Op.SUM) {
            return new SumAggregator();
        } else if (this.what == Op.MIN) {
            return new MinAggregator();
        } else if (this.what == Op.MAX) {
            return new MaxAggregator();
        } else if (this.what == Op.COUNT) {
            return new CountAggregator();
        } else if (this.what == Op.AVG) {
            return new AvgAggregator();
        }
        return null;
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field value = tup.getField(this.afield);
        if (this.gbfield == Aggregator.NO_GROUPING) {
            this.singleAggregator.add(value);
        } else {
            Field key = tup.getField(this.gbfield);
            if (!this.groups.containsKey(key)) {
                this.groups.put(key, newAggregator());
            }
            this.groups.get(key).add(value);
        }
    }

    /**
     * Create a OpIterator over group aggregate results.
     * 
     * @return a OpIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public OpIterator iterator() {
        if (this.gbfield == Aggregator.NO_GROUPING) {
            // Only need to return a iterator with only one value
            TupleDesc td = new TupleDesc(new Type[]{Type.INT_TYPE});
            Tuple aggregateVal = new Tuple(td);
            aggregateVal.setField(0, this.singleAggregator.compute());
            return new TupleIterator(td, Arrays.asList(aggregateVal));

        } else {
            TupleDesc td = new TupleDesc(new Type[]{this.gbfieldtype, Type.INT_TYPE});

            ArrayList<Tuple> res = new ArrayList<>();
            for (Map.Entry<Field, CustomAggregator> group : this.groups.entrySet()) {
                Tuple t = new Tuple(td);
                t.setField(0, group.getKey());
                t.setField(1, group.getValue().compute());
                res.add(t);
            }
            return new TupleIterator(td, res);
        }
    }

}
