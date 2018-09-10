package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Keep track all local branches and access specific branch.
 *  @author Weifeng Dong
 */
public class BranchesManager extends Manager {
    /** directory name for local branches. */
    private static final String BRANCHES = "branches";

    /** map of branchName -> branch, to track all existing branches. */
    private HashMap<String, Reference> _tracker;

    /**
     * Constructor.
     * @param base directory destination.
     */
    public BranchesManager(Path base) {
        super(base.resolve(BRANCHES));
        _tracker = new HashMap<>();
        this.open();
    }

    @Override
    public void open() {
        if (Files.exists(getBase())) {
            try {
                List<Path> paths = Files.list(getBase())
                        .collect(Collectors.toList());

                for (Path path :paths) {
                    String fileName = path.getFileName().toString();
                    Reference ref = get(fileName, Reference.class);
                    put(ref);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        try {
            for (Path path : Files.newDirectoryStream(getBase())) {
                String branchName = path.getFileName().toString();
                if (!_tracker.containsKey(branchName)) {
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
     * @param branchName input.
     */
    public void remove(String branchName) {
        _tracker.remove(branchName);
    }
}
