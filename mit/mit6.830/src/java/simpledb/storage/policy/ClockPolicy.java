package simpledb.storage.policy;

import java.util.HashMap;

import simpledb.common.Database;
import simpledb.storage.BufferPool;
import simpledb.storage.Page;
import simpledb.storage.PageId;

/**
 * Clock page replacement policy,
 * We use fixed size array to represent the linkedlist
 */
public class ClockPolicy {
    /**
     * Clock page replacement algorithm
     * Whenever a page is set or retrieved, reset the pin to true
     */
    public class Container {
        /**
         * Page id
         */
        public PageId pid;

        /**
         * Page saved in buffer poll
         */
        public Page page;

        /**
         * True means this holder has second chance, false means eviction
         */
        public boolean pin;

        public Container() {
            this.pid = null;
            this.page = null;
            this.pin = false;
        }

        public void setPage(PageId pid, Page item) {
            this.pid = pid;
            this.page = item;
            this.pin = true;
        }

        public PageId getPageId() {
            return this.pid;
        }

        public Page getPage() {
            this.pin = true;
            return this.page;
        }

        public boolean pinned() {
            return this.pin;
        }

        public void unpin() {
            this.pin = false;
        }

        public void pin() {
            this.pin = true;
        }
    }

    /**
     * Number of items this policy can store
     */
    private int capacity;

    /**
     * Current index in the clock
     */
    private int index;

    /**
     * Linkedlist holding values
     */
    private Container[] clock;

    /**
     * For fast access to retrive page
     */
    private HashMap<PageId, Container> map;

    public ClockPolicy(int capacity) {
        this.capacity = capacity;
        this.index = 0;
        this.clock = new Container[capacity];
        for (int i = 0; i < capacity; i++) {
            this.clock[i] = new Container();
        }
        this.map = new HashMap<>();
    }

    public void addPage(PageId pid, Page item) {
        // If page is already in the policy, just pin it again
        if (this.map.containsKey(pid)) {
            this.map.get(pid).pin();
            return;
        }

        while (true) {
            Container c = this.clock[this.index % this.capacity];
            if (c.pinned()) {
                c.unpin();
                this.index++;
            } else {
                if (c.getPageId() != null) {
                    // discard page through BufferPool
                    Database.getBufferPool().discardPage(pid);
                }
                c.setPage(pid, item);
                map.put(pid, c);
                this.index++;
                break;
            }
        }
    }

    public Page getPage(PageId pid) {
        Container c = map.get(pid);
        if (c == null) {
            return null;
        }
        return c.getPage();
    }

    public void removePage(PageId pid) {
        if (this.map.containsKey(pid)) {
            // set the container avaiable for assignment
            Container c = this.map.get(pid);
            c.unpin();
            c.page = null;
            // remove the reference
            map.remove(pid);
        }
    }
}
