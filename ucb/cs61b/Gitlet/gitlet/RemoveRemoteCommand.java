package gitlet;

import gitlet.Reference.Type;

/**
 * Command for remove remote branch (lazy delete).
 * @author Weifeng Dong
 */
public class RemoveRemoteCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        String remoteName = args[0];
        repo.refs().remove(remoteName, Type.REMOTE);
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
