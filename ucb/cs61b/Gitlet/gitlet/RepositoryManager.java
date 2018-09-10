package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Main repository.
 * @author Weifeng Dong
 */
public class RepositoryManager extends Manager {

    /**
     * directory name for objects.
     */
    private static final String GITLET = ".gitlet";

    /**
     * work directory.
     */
    private Path _workDir;

    /**
     * is open.
     */
    private boolean _open;

    /**
     * manager for GitletObjects.
     */
    private ObjectsManager _objects;

    /**
     * manager for References.
     */
    private ReferencesManager _refs;

    /**
     * Constructor.
     *
     * @param workDir work directory.
     */
    public RepositoryManager(Path workDir) {
        super(workDir.resolve(GITLET));

        _workDir = workDir;
        _open = false;
        this.open();
    }

    @Override
    public void open() {
        if (Files.exists(getBase())) {
            _open = true;
            _objects = new ObjectsManager(getBase());
            _refs = new ReferencesManager(getBase());

            _objects.open();
            _refs.open();
        }
    }

    @Override
    public void init() {
        if (this.isOpen()) {
            throw new GitletException("A gitlet version-control "
                    + "system already exists in the current directory.");
        }
        super.init();
        this.open();
        objects().init();
        refs().init();
    }

    @Override
    public void close() {
        objects().close();
        refs().close();
    }

    /**
     * getter for _open.
     *
     * @return _open.
     */
    public boolean isOpen() {
        return _open;
    }

    /**
     * getter for _objects.
     *
     * @return _objects.
     */
    public ObjectsManager objects() {
        return _objects;
    }

    /**
     * getter for _refs.
     *
     * @return _refs.
     */
    public ReferencesManager refs() {
        return _refs;
    }

    /**
     * getter for _workDir.
     *
     * @return _workDir.
     */
    public Path getWorkDir() {
        return _workDir;
    }

    /**
     * @return commit which gitlet is currently at.
     */
    public Commit getHeadCommit() {
        return objects().getByHash(getHeadCommitHash(), Commit.class);
    }

    /**
     * @param c input.
     * @return previous commit by given commit.
     */
    public Commit getPreviousCommit(Commit c) {
        return objects().getByHash(c.getParent(), Commit.class);
    }

    /**
     * @return commitHash which gitlet is currently at.
     */
    public String getHeadCommitHash() {
        return refs().getCurrentBranch().target();
    }

    /**
     * revert file into specific version.
     *
     * @param commit target commit.
     * @param fileName input.
     * @param staged determine if file will be put into staging area.
     */
    public void checkoutFile(Commit commit, String fileName, boolean staged) {
        String fileHash = commit.get(fileName);
        Blob blob = objects().getByHash(fileHash, Blob.class);
        Path filePath = getWorkDir().resolve(fileName);

        try {
            Files.write(filePath, blob.getContents());
        } catch (IOException e) {
            e.printStackTrace();
        }
        objects().info().checkout(fileName, fileHash, staged);
    }

    /**
     * revert commit.
     *
     * @param commit       input
     * @param checkChange determine if performing check untracked files.
     */
    public void checkoutCommit(Commit commit, boolean checkChange) {
        State info = objects().info();

        try {
            if (checkChange && info.changed()) {
                throw new GitletException("There is an untracked "
                        + "file in the way; delete it or add it first.");
            }

            for (Path path : Files.newDirectoryStream(getWorkDir(),
                    x -> !Files.isDirectory(x))) {
                Files.delete(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        commit.getBlobs().forEach((fileName, hash) -> {
                Blob blob = objects().getByHash(hash, Blob.class);
                Path dest = getWorkDir().resolve(fileName);
                Utils.writeContents(dest, blob.getContents());
            });

        info.setBlobsIndex(commit.getBlobs());
    }

    /**
     * @param hash hash.
     * @param type expected type.
     * @param <T>  generic type.
     * @return GitletObject by given hash (key).
     */
    public <T extends GitletObject> T getByHash(String hash, Class<T> type) {
        GitletObject o = null;
        if (hash == null || hash.isEmpty()) {
            if (Commit.class == type) {
                o = getHeadCommit();
            }
        } else if (Utils.UID_LENGTH == hash.length()) {
            o = objects().getByHash(hash, type);
        } else {
            o = objects().getByShortHash(hash, type);
        }
        return type.cast(o);
    }

    /**
     * compare current commit wht given commit.
     * @param commit given commit.
     * @return true if not identical
     */
    public boolean compare(Commit commit) {
        try {
            for (Path path : Files.newDirectoryStream(getWorkDir(),
                    x -> !Files.isDirectory(x))) {
                String fileName = path.getFileName().toString();
                if (commit.containsFile(fileName)
                        && objects().info().isUntracked(fileName)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * perform update commit.
     * @param newCommit input.
     */
    public void update(Commit newCommit) {
        refs().updateLocalCommit(newCommit.getHash());
        objects().info().setBlobsIndex(newCommit.getBlobs());
    }

    /**
     * Get history of commit (similar to LogCommand).
     *
     * @param commitHash commit hash.
     * @return list of commit hash (latest to earliest)
     */
    public List<String> getCommitHistory(String commitHash) {
        Commit c = objects().getByHash(commitHash, Commit.class);
        List<String> result = new ArrayList<>();
        while (c != null) {
            result.add(c.getHash());
            c = getPreviousCommit(c);
        }
        return result;
    }
}
