package simpledb.execution.aggregator;

import simpledb.storage.Field;
import simpledb.storage.IntField;

public class CountAggregator implements CustomAggregator {

    private int count;

    public CountAggregator() {
        this.count = 0;
    }

    @Override
    public void add(Field value) {
        this.count += 1;
    }

    @Override
    public Field compute() {
        return new IntField(this.count);
    }
}
