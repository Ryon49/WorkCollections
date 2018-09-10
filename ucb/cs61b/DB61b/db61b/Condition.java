package db61b;

import db61b.Table.Row;

import java.util.List;

/** Represents a single 'where' condition in a 'select' command.
 *  @author Weifeng Dong */
class Condition {

    /** A Condition representing COL1 RELATION COL2, where COL1 and COL2
     *  are column designators. and RELATION is one of the
     *  strings "<", ">", "<=", ">=", "=", or "!=". */
    Condition(Column col1, String relation, Column col2) {
        _col1 = col1;
        _col2 = col2;
        _relation = relation;
    }

    /** A Condition representing COL1 RELATION 'VAL2', where COL1 is
     *  a column designator, VAL2 is a literal value (without the
     *  quotes), and RELATION is one of the strings "<", ">", "<=",
     *  ">=", "=", or "!=".
     */
    Condition(Column col1, String relation, String val2) {
        this(col1, relation, (Column) null);
        _val2 = val2;
    }

    /** Assuming that ROWS are row indices in the respective tables
     *  from which my columns are selected, returns the result of
     *  performing the test I denote. */
    boolean test(Row... rows) {
        int compare = compareTo(rows);
        return checkRelation(compare);
    }

    /** Return true iff ROWS satisfies all CONDITIONS. */
    static boolean test(List<Condition> conditions, Row... rows) {
        for (Condition cond : conditions) {
            if (!cond.test(rows)) {
                return false;
            }
        }
        return true;
    }


    /** The operands of this condition.  _col2 is null if the second operand
     *  is a literal. */
    private Column _col1, _col2;
    /** Second operand, if literal (otherwise null). */
    private String _val2;

    /** Operand the tells the relation between _col1 and _col2 (or _val2_). */
    private String _relation;

    private boolean checkEqual(int compare) {
        return compare == 0;
    }

    private boolean checkGreater(int compare) {
        return compare > 0;
    }

    /**
     * check relationship based on the result from method compareTo.
     */
    private boolean checkRelation(int compare) {
        switch (_relation) {
        case "<":
            return !checkGreater(compare);
        case ">":
            return checkGreater(compare);
        case "<=":
            return checkEqual(compare) || !checkGreater(compare);
        case ">=":
            return checkEqual(compare) || checkGreater(compare);
        case "=":
            return checkEqual(compare);
        case "!=":
            return !checkEqual(compare);
        default:
            return false;
        }
    }

    /**
     * Compare the value between _col1 and _col2 in the row.
     */
    private int compareTo(Row... rows) {
        int compare;
        if (_col2 == null) {
            compare = _col1.getFrom(rows).compareTo(_val2);
        } else {
            compare = _col1.getFrom(rows).compareTo(_col2.getFrom(rows));
        }
        return compare;
    }
}
