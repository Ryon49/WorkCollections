package gitlet;

import java.util.stream.Collectors;

/**
 * Command for finding all commits by message.
 * @author Weifeng Dong
 */
public class FindCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        String msg = args[0];
        String result = repo.objects().tracked()
                .stream()
                .filter((c -> c.getClass() == Commit.class))
                .map(c -> (Commit) c)
                .filter(c -> msg.equals(c.getMessage()))
                .map(Commit::getHash)
                .collect(Collectors.joining("\n"));
        if (result == null || "".equals(result)) {
            throw new GitletException(
                    "Found no commit with that message.");
        } else {
            System.out.println(result);
        }
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
