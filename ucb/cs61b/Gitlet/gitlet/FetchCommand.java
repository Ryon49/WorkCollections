package gitlet;

import gitlet.Reference.Type;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Command for fetching objects from remote repo.
 * @author Weifeng Dong
 */
public class FetchCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        fetch(repo, args[0], args[1]);
    }

    /**
     * pull objects.
     * @param localRepo local repo.
     * @param remoteRepo remote repo.
     * @param remoteHead head commit hash of remote repo.
     */
    private static void pull(RepositoryManager localRepo,
                                    RepositoryManager remoteRepo,
                                    String remoteHead) {
        List<GitletObject> collection = new ArrayList<>();

        for (String hash : remoteRepo.getCommitHistory(remoteHead)) {
            Commit c = remoteRepo.objects().get(hash, Commit.class);
            List<GitletObject> blobs = c.getBlobs().values().stream()
                    .filter(h -> localRepo.objects().containsKey(h))
                    .map((h) -> remoteRepo.objects()
                            .getByHash(h, Blob.class))
                    .collect(Collectors.toList());

            if (localRepo.objects().containsKey(hash)) {
                collection.add(c);
            }

            collection.addAll(blobs);
        }
        localRepo.objects().putAll(collection);

    }

    @Override
    public boolean requiresRepo() {
        return true;
    }

    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 2;
    }

    /**
     * fetch objects.
     * @param repo local repo.
     * @param remoteName remote name.
     * @param remoteBranchName remote branch name.
     */
    public static void fetch(RepositoryManager repo,
                             String remoteName, String remoteBranchName) {
        Reference remoteRef = repo.refs()
                .getByName(remoteName, Type.REMOTE);

        Path remoteDir = repo.getWorkDir()
                .resolve(remoteRef.target()).normalize();

        if (!Files.exists(remoteDir)) {
            throw new GitletException("Remote directory not found.");
        }
        RepositoryManager remoteRepo =
                new RepositoryManager(remoteDir.resolve("..").normalize());

        if (!remoteRepo.isOpen()) {
            throw new GitletException("Remote directory not found.");
        }

        Reference remoteHead = remoteRepo.refs()
                .getByName(remoteBranchName, Type.Branch);

        if (remoteHead == null) {
            throw new GitletException(
                    "That remote does not have that branch.");
        }

        String localBranch = remoteName + "/" + remoteBranchName;
        pull(repo, remoteRepo, remoteHead.target());

        if (!repo.refs().contains(localBranch, Type.Branch)) {
            repo.refs().put(new Reference(remoteHead.name(),
                    remoteHead.target(), Type.Branch));
        } else {
            repo.refs().getByName(localBranch, Type.Branch)
                    .setHead(remoteHead.target());
        }
    }
}
