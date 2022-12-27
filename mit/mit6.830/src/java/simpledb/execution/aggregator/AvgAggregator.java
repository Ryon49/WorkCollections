package simpledb.execution.aggregator;

import simpledb.storage.Field;
import simpledb.storage.IntField;

public class AvgAggregator implements CustomAggregator {

    private int sum;

    private int count;

    public AvgAggregator() {
        this.sum = 0;
        this.count = 0;
    }

    @Override
    public void add(Field value) {
        this.sum += ((IntField)value).getValue();
        this.count += 1;
    }

    @Override
    public Field compute() {
        return new IntField(this.sum / this.count);
    }
    
}
