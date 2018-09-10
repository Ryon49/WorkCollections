package db61b;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static db61b.Utils.error;

/** A single table in a database.
 *  @author P. N. Hilfinger
 */
class Table {
    /** A new Table whose columns are given by COLUMNTITLES, which may
     *  not contain duplicate names. */
    Table(String[] columnTitles) {
        if (columnTitles.length == 0) {
            throw error("table must have at least one column");
        }

        for (int i = columnTitles.length - 1; i >= 1; i -= 1) {
            for (int j = i - 1; j >= 0; j -= 1) {
                if (columnTitles[i].equals(columnTitles[j])) {
                    throw error("duplicate column name: %s",
                                columnTitles[i]);
                }
            }
        }

        _titles = columnTitles;
        _columnCount = _titles.length;

        _rows = new ArrayList<>();
    }

    /** A new Table whose columns are give by COLUMNTITLES. */
    Table(List<String> columnTitles) {
        this(columnTitles.toArray(new String[columnTitles.size()]));
    }

    /** Return the number of columns in this table. */
    private int columns() {
        return _columnCount;
    }

    /** Return the title of the Kth column.  Requires 0 <= K < columns(). */
    private String getTitle(int k) {
        if (k < 0 || k >= _columnCount) {
            return null;
        }
        return _titles[k];
    }

    /**
     * Return all rows in a List structure.
     */
    private List<Row> getRows() {
        return _rows;
    }

    /** Return the number of the column whose title is TITLE, or -1 if
     *  there isn't one. */
    int findColumn(String title) {
        if (title == null) {
            return -1;
        }
        for (int i = 0; i < _titles.length; i++) {
            if (_titles[i].equals(title)) {
                return i;
            }
        }
        return -1;

    }

    /** Add a new row whose column values are VALUES to me if no equal
     *  row already exists.  Return true if anything was added,
     *  false otherwise. */
    private boolean add(Row row) {
        boolean b = hasSameRow(row);
        if (!b) {
            _rows.add(row);
            return true;
        }
        return false;
    }

    /**
     * used to check if has duplicate row in _rows
     */
    private boolean hasSameRow(Row row) {
        for (Row r: _rows) {
            if (compareRows(r, row) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * used to adapt skeleton code.
     */
    boolean add(String[] data) {
        return add(new Row(data));
    }

    /** Read the contents of the file NAME.db, and return as a Table.
     *  Format errors in the .db file cause a DBException. */
    static Table readTable(String name) {
        BufferedReader input;
        Table table;
        input = null;
        try {
            input = new BufferedReader(new FileReader(name + ".db"));
            String header = input.readLine();
            if (header == null) {
                throw error("missing header in DB file");
            }
            String[] columnNames = header.split(",");

            table = new Table(columnNames);

            input.lines()
                    .forEach(line -> table.add(line.split(",")));
        } catch (FileNotFoundException e) {
            throw error("could not find %s.db", name);
        } catch (IOException e) {
            throw error("problem reading from %s.db", name);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.out.println("Opps, I don't know what is happening");
                }
            }
        }
        return table;
    }

    /** Write the contents of TABLE into the file NAME.db. Any I/O errors
     *  cause a DBException. */
    void writeTable(String name) {
        PrintStream output;
        output = null;
        try {
            String sep = ",";
            output = new PrintStream(name + ".db");

            output.println(String.join(sep, _titles));


            for (Row row : _rows) {
                output.println(row.toWritableString());
            }

        } catch (IOException e) {
            throw error("trouble writing to %s.db", name);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /** Print my contents on the standard output, separated by spaces
     *  and indented by two spaces.
     *  @param withTitle determine whether or not to print _titles
     *  */
    void print(boolean withTitle) {

        String sep = " ";
        if (withTitle) {
            System.out.println(String.join(sep, _titles));
        }

        _rows.stream()
                .sorted(this::compareRows)
                .map(Row::toPrintableString)
                .forEach(System.out::println);
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected from
     *  rows of this table that satisfy CONDITIONS. */
    Table select(List<String> columnNames, List<Condition> conditions) {
        Table result = new Table(columnNames);

        if (conditions.isEmpty()) {
            for (Row row : _rows) {
                Row newRow = new Row(row, columnNames);
                result.add(newRow);
            }
        } else {
            for (Row row : _rows) {
                if (Condition.test(conditions, row)) {
                    Row newRow = new Row(row, columnNames);
                    result.add(newRow);
                }
            }
        }
        return result;
    }

    /** Return a new Table whose columns are COLUMNNAMES, selected
     *  from pairs of rows from this table and from TABLE2 that match
     *  on all columns with identical names and satisfy CONDITIONS. */
    Table select(Table table2, List<String> columnNames,
                 List<Condition> conditions) {

        Table result = new Table(columnNames);
        List<Column> columns = new ArrayList<>();
        for (String columnName : columnNames) {
            Column c = new Column(columnName, this, table2);
            columns.add(c);
        }

        List<Column> common1 = new ArrayList<>(),
                common2 = new ArrayList<>();
        for (String name : _titles) {
            int i = table2.findColumn(name);
            if (i >= 0) {
                common1.add(new Column(name, this));
                common2.add(new Column(table2.getTitle(i), table2));
            }
        }
        for (Row row1 : _rows) {
            for (Row row2: table2.getRows()) {
                if (equijoin(common1, common2,
                        row1, row2) && Condition.test(conditions, row1, row2)) {
                    Row r = new Row(row1, row2, columns);
                    result.add(r);
                }
            }
        }
        return result;
    }

    /** Return <0, 0, or >0 depending on whether the row formed from
     *  the elements _columns[0].get(K0), _columns[1].get(K0), ...
     *  is less than, equal to, or greater than that formed from elememts
     *  _columns[0].get(K1), _columns[1].get(K1), ....  This method ignores
     *  the _index. */
    private int compareRows(Row row1, Row row2) {
        for (int i = 0; i < columns(); i += 1) {
            int c = row1.get(i).compareTo(row2.get(i));
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    /** Return true if the columns COMMON1 from ROW1 and COMMON2 from
     *  ROW2 all have identical values.  Assumes that COMMON1 and
     *  COMMON2 have the same number of elements and the same names,
     *  that the columns in COMMON1 apply to this table, those in
     *  COMMON2 to another, and that ROW1 and ROW2 are indices, respectively,
     *  into those tables. */
    private static boolean equijoin(List<Column> common1, List<Column> common2,
                                    Row row1, Row row2) {
        for (int i = 0; i < common1.size(); i++) {
            String v1 = common1.get(i).getFrom(row1);
            String v2 = common2.get(i).getFrom(row2);
            if (!v1.equals(v2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Each row object represents one row of data that stored in the table.
     */
    class Row {
        /**
         * Constructor a new row with data
         */
        Row(String[] data) {
            _data = data;
        }

        /**
         * Constructor a new row with an existed row filtered by columnNames
         */
        Row(Row row, List<String> columnNames) {
            int columnId;
            _data = new String[columnNames.size()];
            for (int i = 0; i < _data.length; i++) {
                columnId = findColumn(columnNames.get(i));
                _data[i] = row.get(columnId);
            }
        }

        /**
         * Constructor a new row with two rows filtered by columnNames
         */
        Row(Row row1, Row row2, List<Column> columns) {
            _data = new String[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                _data[i] = (columns.get(i)).getFrom(row1, row2);
            }
        }

        /**
         * return the kth element of _data.
         */
        String get(int k) {
            return _data[k];
        }

        /**
         * return a string which is formatted to be written in .db file.
         */
        String toWritableString() {
            String sep = ",";
            StringJoiner joiner = new StringJoiner(sep);
            for (String field : _data) {
                joiner.add(field);
            }
            return joiner.toString();
        }

        /**
         * return a string which is formatted to be showed in the console.
         */
        String toPrintableString() {
            String sep = " ";
            StringBuilder builder = new StringBuilder(sep);
            for (String field : _data) {
                builder.append(sep)
                        .append(field);
            }
            return builder.toString();
        }

        private String[] _data;
    }

    /** My column titles. */
    private final String[] _titles;
    /** My columns. Row i consists of _columns[k].get(i) for all k. */
    private final List<Row> _rows;

    /** My number of columns (redundant, but convenient). */
    private final int _columnCount;

}
