package gitlet;

import gitlet.Reference.Type;
/**
 * Command for checkout branch or file.
 *
 * @author Weifeng Dong
 */
public class CheckoutCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        int argLength = args.length;
        if (argLength == 1) {
            checkoutBranch(repo, args[0]);
        } else if (argLength == 2) {
            checkoutFile(repo, args[1], null);
        } else if (argLength == 3) {
            checkoutFile(repo, args[2], args[0]);
        }
    }

    /**
     * set current branch to be target branch.
     *
     * @param repo       current repository.
     * @param branchName target branch name.
     */
    private void checkoutBranch(RepositoryManager repo, String branchName) {
        if (!repo.refs().contains(branchName, Type.Branch)) {
            throw new GitletException("No such branch exists.");
        }
        if (repo.refs().getCurrentBranch().name().equals(branchName)) {
            throw new GitletException(
                    "No need to checkout the current branch.");
        }

        Reference targetBranch = repo.refs().
                getByName(branchName, Type.Branch);
        Commit checked = repo.getByHash(targetBranch.target(), Commit.class);

        if (repo.compare(checked)) {
            throw new GitletException("There is an untracked file in "
                    + "the way; delete it or add it first.");
        }

        repo.refs().setCurrentBranch(targetBranch);
        repo.checkoutCommit(checked, true);
        repo.objects().info().setBlobsIndex(checked.getBlobs());
    }

    /**
     * checkout target file from given commit,
     * if commitHash is null, assuming getByName the file from current commit.
     *
     * @param repo       current repository.
     * @param fileName   target file name.
     * @param commitHash target commitHash.
     */
    public static void checkoutFile(RepositoryManager repo,
                              String fileName, String commitHash) {
        Commit c = repo.getByHash(commitHash, Commit.class);

        if (c == null) {
            throw new GitletException("No commit with that id exists.");
        }
        if (!c.getBlobs().containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        }
        repo.checkoutFile(c, fileName, false);
    }

    @Override
    public boolean requiresRepo() {
        return true;
    }

    @Override
    public boolean checkOperands(String[] args) {

        return (args.length == 1
                || args.length == 2 && "--".equals(args[0]))
                || (args.length == 3 && "--".equals(args[1]));
    }
}
