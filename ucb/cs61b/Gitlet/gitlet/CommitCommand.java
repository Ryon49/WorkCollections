package gitlet;

import java.time.Instant;

/**
 * Command for creating new commit.
 * @author Weifeng Dong
 */
public class CommitCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        String msg = args[0];
        if (msg.isEmpty()) {
            throw new GitletException("Please enter a commit message.");
        }

        State info = repo.objects().info();

        Commit head = repo.getHeadCommit();
        Commit c = new Commit(msg,
                head.getHash(),
                Instant.now(),
                info.newBlobsIndex(head.getBlobs()));
        addCommit(repo, c);
    }

    /**
     * add a new commit to repo.
     * @param repo current repository
     * @param newCommit new commit.
     */
    public static void addCommit(RepositoryManager repo, Commit newCommit) {
        repo.objects().put(newCommit.getHash(), newCommit);
        repo.update(newCommit);
    }

    @Override
    public boolean requiresRepo() {
        return true;
    }

    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 1;
    }
}
