package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

/** Keep track all remote branches and access specific branch.
 *  @author Weifeng Dong
 */
public class RemotesManager extends Manager {

    /**
     * directory name for remote branches.
     */
    private static final String REMOTES = "remotes";

    /**
     * map of branchName -> branch, to track all existing branches.
     */
    private HashMap<String, Reference> _tracker;

    /**
     * Constructor.
     *
     * @param base input.
     */
    public RemotesManager(Path base) {
        super(base.resolve(REMOTES));
        _tracker = new HashMap<>();
        this.open();
    }

    @Override
    public void open() {
        if (Files.exists(getBase())) {
            try {
                Files.list(getBase())
                        .map(path -> path.getFileName().toString())
                        .forEach(name -> put(get(name, Reference.class)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        try {
            for (Path path : Files.newDirectoryStream(getBase())) {
                String name = path.getFileName().toString();
                if (!_tracker.containsKey(name)) {
                    Files.delete(path);
                }
            }
            _tracker.forEach(this::save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * add new branch to _tracker.
     *
     * @param ref branch.
     */
    public void put(Reference ref) {
        _tracker.put(ref.name(), ref);
    }

    /**
     * @return set of tracked branchName.
     */
    public Set<String> tracked() {
        return _tracker.keySet();
    }

    /**
     * retrieve branch from _tracker by name.
     *
     * @param branchName input.
     * @return existed branch.
     */
    public Reference getByName(String branchName) {
        return _tracker.get(branchName);
    }

    /**
     * @param branchName input
     * @return true if given branch is existed in _tracker.
     */
    public boolean contains(String branchName) {
        return _tracker.containsKey(branchName);
    }

    /**
     * remove specific branch from _tracker by name.
     *
     * @param branchName input.
     */
    public void remove(String branchName) {
        _tracker.remove(branchName);
    }

}
