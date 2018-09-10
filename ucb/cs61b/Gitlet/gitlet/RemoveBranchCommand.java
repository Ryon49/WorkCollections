package gitlet;

import gitlet.Reference.Type;

/**
 * Command for removing branch.
 * @author Weifeng Dong
 */
public class RemoveBranchCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        String targetBranch = args[0];
        if (targetBranch.equals(repo.refs().getCurrentBranch().name())) {
            throw new GitletException(
                    "Cannot remove the current branch.");
        }

        repo.refs().remove(targetBranch, Type.Branch);
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
