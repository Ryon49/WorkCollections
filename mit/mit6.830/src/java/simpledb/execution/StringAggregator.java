package simpledb.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import simpledb.common.Type;
import simpledb.execution.aggregator.CountAggregator;
import simpledb.storage.Field;
import simpledb.storage.Tuple;
import simpledb.storage.TupleDesc;
import simpledb.storage.TupleIterator;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

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
     * Used when NO_GROUPING is used
     */
    private CountAggregator singleAggregator;

    /**
     * Manage each groupby state if using gruops
     */
    private HashMap<Field, CountAggregator> groups;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        if (what != Op.COUNT) {
            throw new IllegalArgumentException("StringAggregator can only be used with Op.COUNT");
        }
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.singleAggregator = new CountAggregator();
        this.groups = new HashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field value = tup.getField(this.afield);
        if (this.gbfield == Aggregator.NO_GROUPING) {
            this.singleAggregator.add(value);
        } else {
            Field key = tup.getField(this.gbfield);
            if (!this.groups.containsKey(key)) {
                this.groups.put(key, new CountAggregator());
            }
            this.groups.get(key).add(value);
        }
    }

    /**
     * Create a OpIterator over group aggregate results.
     *
     * @return a OpIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
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
            for (Map.Entry<Field, CountAggregator> group : this.groups.entrySet()) {
                Tuple t = new Tuple(td);
                t.setField(0, group.getKey());
                t.setField(1, group.getValue().compute());
                res.add(t);
            }
            return new TupleIterator(td, res);
        }
    }

}
