package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * abstract manager.
 * @author Weifeng Dong
 */
public abstract class Manager {

    /** address of current directory for current manager. */
    private Path _base;

    /**
     * Constructor.
     * @param base input.
     */
    public Manager(Path base) {
        _base = base;
    }

    /**
     * create directory for current manager.
     */
    public void init() {
        if (!Files.exists(getBase())) {
            try {
                Files.createDirectories(getBase());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * load tracker objects.
     */
    public abstract void open();

    /**
     * getter for _base.
     * @return _base.
     */
    public Path getBase() {
        return _base;
    }

    /**
     * save _trackers to directory.
     */
    public abstract void close();

    /**
     * save a object.
     * @param path target dest (relative path).
     * @param obj target object.
     */
    public void save(String path, Serializable obj) {
        Path dest = getBase().resolve(path);
        try {
            if (!Files.exists(dest.getParent())) {
                Files.createDirectories(dest.getParent());
            }
            Utils.writeObject(dest, obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieve object.
     * @param path target dest
     * @param type expected type
     * @param <T> generic type
     * @return object that is casted into expected type.
     */
    public <T extends Serializable> T get(String path, Class<T> type) {
        Path target = getBase().resolve(path);
        return Utils.readObject(target, type);
    }
}
