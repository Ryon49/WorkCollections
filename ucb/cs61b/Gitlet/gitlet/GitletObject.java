package gitlet;

import java.io.Serializable;

/**
 * parent of Blob and Commit object.
 * @author Weifeng Dong
 */
public abstract class GitletObject implements Serializable {

    /**
     * getter for _hash.
     *
     * @return hash of GitletObject.
     */
    public abstract String getHash();
}
