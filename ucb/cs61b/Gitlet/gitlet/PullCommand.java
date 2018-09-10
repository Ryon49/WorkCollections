package gitlet;

/**
 * Command for pulling from remote.
 *
 * @author Weifeng Dong
 */
public class PullCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        FetchCommand.fetch(repo, args[0], args[1]);
        MergeCommand.merge(repo, args[0] + "/" + args[1]);
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
