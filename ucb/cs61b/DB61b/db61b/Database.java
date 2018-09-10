package db61b;

import java.util.HashMap;
import java.util.Map;

/** A collection of Tables, indexed by name.
 *  @author Weifeng Dong */
class Database {

    /** An empty database. */
    public Database() {
        db = new HashMap<>();
    }

    /** Return the Table whose name is NAME stored in this database, or null
     *  if there is no such table. */
    Table get(String name) {
        return db.getOrDefault(name, null);
    }

    /** Set or replace the table named NAME in THIS to TABLE.  TABLE and
     *  NAME must not be null, and NAME must be a valid name for a table. */
    void put(String name, Table table) {
        if (name == null || table == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (db.containsKey(name)) {
            db.replace(name, table);
        } else {
            db.put(name, table);
        }
    }

    private Map<String, Table> db;

}
