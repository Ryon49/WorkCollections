package simpledb.storage;

import simpledb.common.Type;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        return null;
    }

    private static final long serialVersionUID = 1L;

    /**
     * The collection of TDItem that represents scheme of Tuple
     * */
    private ArrayList<TDItem> scheme;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
        this.scheme = new ArrayList<>(typeAr.length);
        for (int i = 0; i < typeAr.length; i++) {
            if (fieldAr == null) {
                this.scheme.add(new TDItem(typeAr[i], null));
            } else {
                this.scheme.add(new TDItem(typeAr[i], fieldAr[i]));
            }
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        this(typeAr, null);
    }

    /**
     * Private construct only used by merge()
     */
    private TupleDesc(ArrayList<TDItem> scheme) {
        this.scheme = scheme;
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return this.scheme.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        if (i < 0 || i >= this.scheme.size()) {
            throw new NoSuchElementException("Invalid index found");
        }
        return this.scheme.get(i).fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        if (i < 0 || i >= this.scheme.size()) {
            throw new NoSuchElementException("Invalid index found");
        }
        return this.scheme.get(i).fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        int size = this.scheme.size();
        for (int i = 0; i < size; i++) {
            String fieldName = this.getFieldName(i);
            if (fieldName == null) {
                continue;
            }
            if (fieldName.equals(name)) {
                return i;
            }
        }
        throw new NoSuchElementException(String.format("%s is not a valid field name", name));
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        int res = 0;
        for (TDItem item : this.scheme) {
            res += item.fieldType.getLen();
        }
        return res;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        ArrayList<TDItem> combined = new ArrayList<>(); 
        combined.addAll(td1.scheme);
        combined.addAll(td2.scheme);
        return new TupleDesc(combined);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        TupleDesc other = (TupleDesc)o;
        if (this.numFields() != other.numFields() || this.getSize() != other.getSize()) {
            return false;
        }
        int size = this.numFields();
        for (int i = 0; i < size; i++) {
            if (this.getFieldType(i) != other.getFieldType(i)) {
                return false;
            }
            String fieldName = this.getFieldName(i);
            String otherFileName = other.getFieldName(i);
            if (fieldName == null && otherFileName == null) {
                continue;
            }
            if ((fieldName == null && otherFileName != null ) || 
                (fieldName != null && otherFileName == null) || 
                !fieldName.equals(otherFileName)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        int res = 0;
        for (int i = 0; i < this.scheme.size(); i++) {
            int itemHash = this.scheme.get(i).hashCode();
            res = res ^ i ^ itemHash;
        }
        return res;
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        StringJoiner joiner = new StringJoiner(", ");
        for (TDItem item : this.scheme) {
            joiner.add(item.toString());
        }
        return joiner.toString();
    }
}
