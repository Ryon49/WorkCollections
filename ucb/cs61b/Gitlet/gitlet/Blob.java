package gitlet;

/** Store contents and hash of a file.
 *  @author Weifeng Dong
 */
public class Blob extends GitletObject {

    /** content of file. */
    private final byte[] _contents;

    /** SHA-1 hash. */
    private String _hash;

    /**
     * Constructor.
     * @param contents file contents.
     */
    public Blob(byte[] contents) {
        _contents = contents;
        _hash = Utils.sha1(contents);
    }

    /**
     * @return getter for contents.
     */
    public byte[] getContents() {
        return _contents;
    }

    @Override
    public String getHash() {
        return _hash;
    }
}
