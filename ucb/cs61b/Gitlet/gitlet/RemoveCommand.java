package gitlet;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Command for remove unstaged and staged file.
 * @author Weifeng Dong
 */
public class RemoveCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        remove(repo, args[0], repo.getHeadCommit());
    }

    /**
     * @param repo current repository
     * @param fileName fileName.
     * @param commit commit.
     */
    public static void remove(RepositoryManager repo,
                              String fileName, Commit commit) {
        State index = repo.objects().info();

        if (commit.containsFile(fileName)) {
            try {
                index.remove(fileName, true);
                if (Files.exists(repo.getWorkDir().resolve(fileName))) {
                    Files.delete(repo.getWorkDir().resolve(fileName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            index.remove(fileName, false);
        }
    }

    @Override
    public boolean requiresRepo() {
        return true;
    }

    @Override
    public boolean checkOperands(String[] args) {
        return args.length >= 1;
    }
}
