package gitlet;

import gitlet.Reference.Type;

/**
 * Command for adding new remote.
 * @author Weifeng Dong
 */
public class AddRemoteCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        String remoteName = args[0];
        String targetDir = args[1];


        Reference ref = new Reference(remoteName,
                targetDir, Type.REMOTE);
        repo.refs().put(ref);
    }

    @Override
    public boolean requiresRepo() {
        return true;
    }

    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 2;
    }
}
