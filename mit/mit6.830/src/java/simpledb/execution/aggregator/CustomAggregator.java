package simpledb.execution.aggregator;

import simpledb.storage.Field;

public interface CustomAggregator {
    
    public void add(Field value);

    public Field compute();
}
