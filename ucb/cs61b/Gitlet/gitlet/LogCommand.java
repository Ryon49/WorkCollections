package gitlet;

/**
 * Command for Logging.
 * @author Weifeng Dong
 */
public class LogCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        Commit head = repo.getHeadCommit();

        boolean isMerge = head.isMergeCommit();
        while (head != null) {
            System.out.println(head.log());
            if (isMerge) {
                printMergedLog(repo, head.getMergeCommitHash());
                isMerge = false;
            }

            head = repo.getPreviousCommit(head);
        }
    }

    /**
     * print merged commit.
     * @param repo current repository.
     * @param mergeCommitHash merged hash.
     */
    private void printMergedLog(RepositoryManager repo,
                                String mergeCommitHash) {
        Commit merged = repo.getByHash(mergeCommitHash, Commit.class);
        System.out.println(merged.log());
    }

    @Override
    public boolean requiresRepo() {
        return true;
    }

    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 0;
    }
}
