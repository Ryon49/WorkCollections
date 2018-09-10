package gitlet;

/**
 * Command for reverting specific commit.
 *
 * @author Weifeng Dong
 */
public class ResetCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        reset(repo, args[0]);
    }

    /**
     * reset branch to a commit.
     *
     * @param repo             current repo.
     * @param targetCommitHash target commit hash.
     */
    public static void reset(RepositoryManager repo, String targetCommitHash) {
        Commit target = repo.getByHash(targetCommitHash, Commit.class);

        if (target == null) {
            throw new GitletException("No commit with that id exists.");
        }
        if (repo.compare(target)) {
            throw new GitletException("There is an untracked file in "
                    + "the way; delete it or add it first.");
        }
        repo.checkoutCommit(target, false);
        repo.update(target);
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
