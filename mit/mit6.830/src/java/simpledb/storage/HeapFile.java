package simpledb.storage;

import simpledb.common.Database;
import simpledb.common.DbException;
import simpledb.common.Debug;
import simpledb.common.Permissions;
import simpledb.transaction.TransactionAbortedException;
import simpledb.transaction.TransactionId;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    private File f;
    private TupleDesc td;
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        this.f = f;
        this.td = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        return this.f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        return this.f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        final int pageSize = BufferPool.getPageSize();
        byte[] data = new byte[pageSize];
        try {
            RandomAccessFile raf = new RandomAccessFile(this.f, "r");
            raf.seek(pid.getPageNumber() * pageSize);
            raf.readFully(data);
            raf.close();

            HeapPageId hpid = new HeapPageId(pid.getTableId(), pid.getPageNumber());
            Page page = new HeapPage(hpid, data);
            return page;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.f, "rw");
        raf.seek(page.getId().getPageNumber() * BufferPool.getPageSize());
        raf.write(page.getPageData());
        raf.close();
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        return (int)Math.ceil(this.f.length() / BufferPool.getPageSize());
    }

    private void addNewPage() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.f, "rw");
        raf.seek(this.f.length());
        raf.write(HeapPage.createEmptyPageData());
        raf.close();
    }

    // see DbFile.java for javadocs
    public List<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // find an existing page and do the insert
        for (int pageNo = 0; pageNo < this.numPages(); pageNo++) {
            HeapPage page = (HeapPage)Database.getBufferPool().getPage(tid, new HeapPageId(getId(), pageNo), Permissions.READ_WRITE);
            if (page.getNumEmptySlots() > 0) {
                page.insertTuple(t);
                return Arrays.asList(page);
            }
        }
        // Allocate a new page, this page will still be in memory
        this.addNewPage();
        HeapPage newPage = (HeapPage)Database.getBufferPool().getPage(tid, new HeapPageId(getId(), this.numPages() - 1), Permissions.READ_WRITE);
        newPage.insertTuple(t);
        return Arrays.asList(newPage);
    }

    // see DbFile.java for javadocs
    public List<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        HeapPage page = (HeapPage)Database.getBufferPool().getPage(tid, t.getRecordId().getPageId(), Permissions.READ_WRITE);
        page.deleteTuple(t);
        return Arrays.asList(page);
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        return new DbFileIterator() {
            private int pgNo;
            private Iterator<Tuple> tupleIterator;
            @Override
            public void open() throws DbException, TransactionAbortedException {
                this.pgNo = 0;
                HeapPage page = (HeapPage)Database.getBufferPool().getPage(tid, new HeapPageId(getId(), pgNo), Permissions.READ_ONLY);
                this.tupleIterator = page.iterator();
            }

            private void nextPage() throws TransactionAbortedException, DbException {
                this.pgNo += 1;
                if (pgNo == numPages()) {
                    this.tupleIterator = null;
                } else {
                    HeapPage page = (HeapPage)Database.getBufferPool().getPage(tid, new HeapPageId(getId(), pgNo), Permissions.READ_ONLY);
                    this.tupleIterator = page.iterator();
                }
            }

            @Override
            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (this.tupleIterator == null) {
                    return false;
                } else if (this.tupleIterator.hasNext()) {
                    return true;
                }
                nextPage();
                return this.hasNext();
            }

            @Override
            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                if (!this.hasNext()) {
                    throw new NoSuchElementException("End of table");
                }
                Tuple t = this.tupleIterator.next();
                return t;
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                this.pgNo = 0;
                this.tupleIterator = ((HeapPage)readPage(new HeapPageId(getId(), pgNo))).iterator();
            }

            @Override
            public void close() {
                this.tupleIterator = null;
            }
        };
    }

}

