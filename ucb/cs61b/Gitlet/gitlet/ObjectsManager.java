package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * used to track all GitletObjects,
 * including Blob and Commit.
 *
 * @author Weifeng Dong
 */
public class ObjectsManager extends Manager {

    /**
     * directory name for objects.
     */
    public static final String OBJECTS = "objects";

    /**
     * file name for index.
     */
    public static final String STATE = "state";

    /**
     * hash -> GitletObject, used to tracker all existing GitletObjects.
     */
    private HashMap<String, GitletObject> _tracker;

    /**
     * index.
     */
    private State _state;

    /**
     * Constructor.
     * @param base directory destination.
     */
    public ObjectsManager(Path base) {
        super(base.resolve(OBJECTS));
        _tracker = new HashMap<>();
    }

    @Override
    public void open() {
        if (Files.exists(getBase())) {
            try {
                _state = get(STATE, State.class);

                Files.list(getBase())
                        .filter(path -> Files.isDirectory(path))
                        .forEach(path -> {
                                try {
                                    for (Path sub
                                            : Files.newDirectoryStream(path)) {
                                        String hash = path.getFileName()
                                                .toString()
                                                .concat(sub.getFileName()
                                                        .toString());
                                        put(hash, get(localPath(hash),
                                                GitletObject.class));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init() {
        super.init();
        _state = new State();
    }

    @Override
    public void close() {
        save(STATE, _state);
        _tracker.forEach((hash, obj)
                -> save(localPath(hash), obj));
    }

    /**
     * put new GitletObject into _tracker.
     * @param hash SHA-1 hash of obj
     * @param obj  tracked objects.
     */
    public void put(String hash, GitletObject obj) {
        _tracker.put(hash, obj);
    }

    /**
     * @param hash input.
     * @return file path representation of hash.
     */
    private String localPath(String hash) {
        return hash.substring(0, 2) + "/"
                + hash.substring(2);
    }

    /**
     * getter for _state.
     * @return _state.
     */
    public State info() {
        return _state;
    }

    /**
     * @param hash input.
     * @param type expected class type of GitletObject (Blob or Commit).
     * @param <T>  generic type.
     * @return GitletObject by given hash (key).
     */
    public <T extends GitletObject> T getByHash(String hash, Class<T> type) {
        if (hash == null || hash.isEmpty()) {
            return null;
        }

        GitletObject result = _tracker.get(hash);
        if (result == null) {
            result = get(localPath(hash), type);
            _tracker.put(hash, result);
        }
        return type.cast(result);
    }

    /**
     * @return all tracked GitletObject
     */
    public Collection<GitletObject> tracked() {
        return _tracker.values();
    }

    /**
     * @param hash hash input (length < 40)
     * @param type expected class type of GitletObject (Blob or Commit).
     * @param <T>  generic type.
     * @return GitletObject by matching hash (key).
     */
    public <T extends GitletObject> T getByShortHash(
            String hash, Class<T> type) {
        int length = hash.length();

        for (String s : _tracker.keySet()) {
            if (hash.equals(s.substring(0, length))) {
                GitletObject obj = _tracker.get(s);
                if (type == obj.getClass()) {
                    return type.cast(obj);
                }
            }
        }
        return null;
    }

    /**
     * @param hash input.
     * @return true if hash existed in _tracker.
     */
    public boolean containsKey(String hash) {
        return _tracker.containsKey(hash);
    }


    /**
     * put collection of GitletObjects into _tracker.
     * @param collection set of GitletObjects.
     */
    public void putAll(List<GitletObject> collection) {
        for (GitletObject o : collection) {
            put(o.getHash(), o);
        }
    }
}
