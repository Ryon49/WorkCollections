package gitlet;

/**
 * Command for printing global log.
 * @author Weifeng Dong
 */
public class GlobalLogCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        repo.objects().tracked()
                .stream()
                .filter((o -> o.getClass() == Commit.class))
                .map(o -> (Commit) o)
                .forEach(commit -> System.out.println(commit.log()));
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
