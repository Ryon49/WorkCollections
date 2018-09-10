package gitlet;

import gitlet.Reference.Type;

/**
 * Command for creating a new branch.
 *
 * @author Weifeng Dong
 */
public class BranchCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        String branchName = args[0];

        if (repo.refs().contains(branchName, Type.Branch)) {
            throw new GitletException(
                    "A branch with that name already exists.");
        }
        Reference ref = new Reference(branchName,
                repo.getHeadCommitHash(), Type.Branch);
        repo.refs().put(ref);
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
