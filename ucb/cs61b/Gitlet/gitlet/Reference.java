package gitlet;

import java.io.Serializable;

/**
 * branch Object.
 *
 * @author Weifeng Dong
 */
public class Reference implements Serializable {

    /**
     * deference type of branch (Branch (local) or Remote).
     */
    public enum Type {
        Branch, REMOTE
    }

    /**
     * name of branch.
     */
    private String _name;

    /**
     * commit which branch is pointing to.
     */
    private String _target;

    /**
     * type of commit.
     */
    private  Type _type;
    /**
     * Constructor.
     * @param name       input.
     * @param targetHash input.
     * @param type type.
     */
    public Reference(String name, String targetHash, Type type) {
        _name = name;
        _target = targetHash;
        _type = type;
    }
    /**
     * getter for _name.
     * @return _name.
     */
    public String name() {
        return _name;
    }

    /**
     * getter for _target.
     * @return _target.
     */
    public String target() {
        return _target;
    }

    /**
     * setter for _target.
     * @param hash input.
     */
    public void setHead(String hash) {
        _target = hash;
    }

    /**
     * getter for _type.
     * @return _type.
     */
    public Type type() {
        return _type;
    }
}
