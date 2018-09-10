package gitlet;

import java.time.Instant;
import java.util.HashMap;

import gitlet.Reference.Type;

/**
 * Command for initialize repository.
 * @author Weifeng Dong
 */
public class InitCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        repo.init();

        Commit commit = new Commit("initial commit",
                "", Instant.EPOCH, new HashMap<>());
        repo.objects().put(commit.getHash(), commit);
        Reference master = new Reference("master",
                commit.getHash(), Type.Branch);
        repo.refs().put(master);
    }

    @Override
    public boolean requiresRepo() {
        return false;
    }

    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 0;
    }
}
