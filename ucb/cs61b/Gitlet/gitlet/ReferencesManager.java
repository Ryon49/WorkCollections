package gitlet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import gitlet.Reference.Type;


/**
 * used to track all Branches references.
 *
 * @author Weifeng Dong
 */
public class ReferencesManager extends Manager {
    /**
     * directory name for objects.
     */
    private static final String REFS = "refs";

    /**
     * file name for local head ref.
     */
    private static final String LOCAL = "local";

    /**
     * file name for remote head ref.
     */
    private static final String REMOTE = "remote";

    /**
     * local branch.
     */
    private Reference _head;

    /**
     * remote branch.
     */
    private Reference _remote;

    /**
     * manager for local branches.
     */
    private BranchesManager _branches;

    /**
     * manager for remote branches.
     */
    private RemotesManager _remotes;


    /**
     * Constructor.
     * @param base base.
     */
    ReferencesManager(Path base) {
        super(base.resolve(REFS));
        _branches = new BranchesManager(getBase());
        _remotes = new RemotesManager(getBase());
        open();
    }

    /**
     * load local head branch from current directory.
     * @return index reference.
     */
    private Reference loadLocalBranch() {
        if (Files.exists(getBase().resolve(LOCAL))) {
            return get(LOCAL, Reference.class);
        }
        return null;
    }

    /**
     * load remote head branch from current directory.
     * @return index reference.
     */
    private Reference loadRemoteBranch() {
        if (Files.exists(getBase().resolve(REMOTE))) {
            return get(REMOTE, Reference.class);
        }
        return null;
    }

    /**
     * getter for _branches.
     * @return _branches.
     */
    public BranchesManager branches() {
        return _branches;
    }

    /**
     * getter for _remote.
     * @return _branches.
     */
    public RemotesManager remotes() {
        return _remotes;
    }

    @Override
    public void init() {
        super.init();
        branches().init();
        remotes().init();
    }

    @Override
    public void open() {
        if (Files.exists(getBase())) {
            _head = loadLocalBranch();
            _remote = loadRemoteBranch();
            branches().open();
            remotes().open();
        }
    }

    @Override
    public void close() {
        save(LOCAL, _head);
        save(REMOTE, _remote);
        _branches.close();
        _remotes.close();
    }

    /**
     * put new reference into trackers.
     * @param ref input reference.
     */
    public void put(Reference ref) {
        if (Type.Branch == ref.type()) {
            if (_head == null) {
                _head = ref;
            }
            branches().put(ref);
        } else if (Type.REMOTE == ref.type()) {
            if (_remote == null) {
                _remote = ref;
            }
            remotes().put(ref);
        }
    }

    /**
     * update commit of index reference.
     * @param hash input.
     */
    public void updateLocalCommit(String hash) {
        _head.setHead(hash);
        put(_head);
    }

    /**
     * @return set of all branch names.
     */
    public Set<String> tracked() {
        return _branches.tracked();
    }

    /**
     * getter for _head.
     * @return _head.
     */
    public Reference getCurrentBranch() {
        return _head;
    }

    /**
     * @param branchName input.
     * @return reference by given name.
     * @param type type.
     */
    public Reference getByName(String branchName, Type type) {

        Reference result = null;
        if (Type.Branch == type) {
            result = branches().getByName(branchName);
        } else if (Type.REMOTE == type) {
            result = remotes().getByName(branchName);
        }
        return result;
    }

    /**
     * setter for _head.
     * @param target input.
     */
    public void setCurrentBranch(Reference target) {
        _head = target;
    }

    /**
     * @param branchName input.
     * @return true if given branch is existed in gitlet.
     * @param type type.
     */
    public boolean contains(String branchName, Type type) {
        if (Type.Branch == type) {
            return branches().contains(branchName);
        } else if (Type.REMOTE == type) {
            return remotes().contains(branchName);
        }
        return false;
    }

    /**
     * remove a branch by name.
     * @param targetBranch input.
     * @param type type.
     */
    public void remove(String targetBranch, Type type) {
        if (Type.Branch == type) {
            if (!branches().contains(targetBranch)) {
                throw new GitletException(
                        "A branch with that name does not exist.");
            }
            branches().remove(targetBranch);
        } else if (Type.REMOTE == type) {
            if (!remotes().contains(targetBranch)) {
                throw new GitletException(
                        "A branch with that name does not exist.");
            }
            remotes().remove(targetBranch);
        }
    }
}
