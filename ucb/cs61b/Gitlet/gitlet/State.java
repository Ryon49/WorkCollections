package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/**
 * used to track staging area.
 * @author Weifeng Dong
 */
public class State implements Serializable {

    /** all blobs. */
    private HashMap<String, String> _blobs;

    /** all staged files. */
    private HashMap<String, String> _staged;

    /** all removed files. */
    private HashMap<String, String> _removed;

    /**
     * Creates a new Info.
     */
    public State() {
        _blobs = new HashMap<>();
        _removed = new HashMap<>();
        _staged = new HashMap<>();
    }

    /**
     * Adds a blob to the staging area.
     * @param fileName  file name of the blob.
     * @param hash hash of the blob.
     */
    public void add(String fileName, String hash) {
        if (_removed.containsKey(fileName)) {
            String removedHash = _removed.remove(fileName);
            _blobs.put(fileName, removedHash);
        } else {
            if (!_blobs.containsKey(fileName)
                    || !_blobs.get(fileName).equals(hash)) {
                _staged.put(fileName, hash);
            }
            _blobs.put(fileName, hash);
        }
    }

    /**
     * Checks out a particular file.
     * @param fileName file to checkout.
     * @param hash hash of the file.
     * @param stage determine if file will be put into staging area.
     */
    public void checkout(String fileName, String hash, boolean stage) {
        if (stage) {
            add(fileName, hash);
        } else {
            _blobs.put(fileName, hash);
            _staged.remove(fileName);
            _removed.remove(fileName);
        }
    }

    /**
     * unstage a file.
     * @param fileName target file name.
     * @param fromLastCommit determine if target file is in last commit.
     */
    public void remove(String fileName, boolean fromLastCommit) {
        if (!_blobs.containsKey(fileName)) {
            throw new GitletException("No reason to remove the file.");
        }
        if (fromLastCommit) {
            _removed.put(fileName, _blobs.get(fileName));
        }

        _staged.remove(fileName);
        _blobs.remove(fileName);
    }

    /**
     * Clears the stage.
     */
    private void clear() {
        _removed.clear();
        _staged.clear();
    }


    /**
     * @return getter for _blobs.
     */
    public HashMap<String, String> getBlobs() {
        return _blobs;
    }

    /**
     * @return getter for _removed.
     */
    public HashMap<String, String> getRemoved() {
        return _removed;
    }

    /**
     * @return getter for _staged.
     */
    public HashMap<String, String> getStaged() {
        return _staged;
    }

    /**
     * Determines anything is added to staging area.
     * @return true if staging area is not empty.
     */
    public boolean changed() {
        return _removed.size() + _staged.size() != 0;
    }

    /**
     * merging input with staging area.
     * @param fromLastCommit tracked blobs from last commit.
     * @return new _blobs for new commit
     */
    public HashMap<String, String> newBlobsIndex(
            HashMap<String, String> fromLastCommit) {
        if (!changed()) {
            throw new GitletException("No changes added to the commit.");
        }
        HashMap<String, String> result = new HashMap<>();

        result.putAll(fromLastCommit);
        result.putAll(getStaged());
        for (String fileName : getRemoved().keySet()) {
            result.remove(fileName);
        }

        setBlobsIndex(result);
        return result;
    }

    /**
     * set new _blobs.
     * @param blobs new _blobs.
     */
    public void setBlobsIndex(HashMap<String, String> blobs) {
        clear();
        _blobs = blobs;
    }

    /**
     * untage file from stage area.
     * @param fileName file name.
     */
    public void unstage(String fileName) {
        if (!_blobs.containsKey(fileName)) {
            throw new IllegalStateException("No reason to remove the file.");
        }

        _staged.remove(fileName);
    }

    /**
     * @param fileName file name.
     * @return true if file is tracked by state.
     */
    public boolean isUntracked(String fileName) {
        return !getBlobs().containsKey(fileName)
                || getStaged().containsKey(fileName);
    }
}
