package gitlet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import gitlet.Reference.Type;

/**
 * Command for pushing to remote.
 *
 * @author Weifeng Dong
 */
public class PushCommand implements Command {
    @Override
    public void execute(RepositoryManager localRepo, String[] args) {
        String remoteName = args[0];
        String remoteBranchName = args[1];

        Reference remoteRef = localRepo.refs()
                .getByName(remoteName, Type.REMOTE);

        Path remoteDir = localRepo.getWorkDir()
                .resolve(remoteRef.target()).normalize();

        if (!Files.exists(remoteDir)) {
            throw new GitletException("Remote directory not found.");
        }
        RepositoryManager remoteRepo =
                new RepositoryManager(remoteDir.resolve("..").normalize());

        if (!remoteRepo.isOpen()) {
            throw new GitletException("Remote directory not found.");
        }

        Reference remoteBranch = remoteRepo.refs()
                .getByName(remoteBranchName, Type.Branch);

        if (remoteBranch == null) {
            remoteBranch = new Reference(
                    remoteBranchName, "", Type.Branch);
            remoteRepo.refs().put(remoteBranch);
        }

        String localHeadHash = localRepo.getHeadCommitHash();
        if (localHeadHash.equals(remoteBranch.target())) {
            return;
        }

        List<String> intersecting =
                intersectBranches(localRepo, remoteRepo,
                        localHeadHash, remoteBranch.target());
        pushCommits(localRepo, remoteRepo, intersecting);
        fastForward(remoteRepo, remoteBranch.target(), localHeadHash);

        remoteRepo.close();
    }

    /**
     * Intersects two branches.
     * @param localRepo  localRepo.
     * @param remoteRepo remoteRepo localRepo.
     * @param localHeadHash local localHeadHash.
     * @param remoteHeadHash remoteRepo localHeadHash.
     * @return The commits in the intersection.
     */
    private static List<String> intersectBranches(
            RepositoryManager localRepo, RepositoryManager remoteRepo,
            String localHeadHash, String remoteHeadHash) {

        List<String> localHistory
                = localRepo.getCommitHistory(localHeadHash);
        if (!localHistory.contains(remoteHeadHash)
                && !remoteHeadHash.isEmpty()) {
            remoteRepo.close();
            throw new IllegalStateException(
                    "Please pull down remoteRepo changes before pushing.");
        }
        List<String> remoteHistory = remoteRepo
                .getCommitHistory(remoteHeadHash);
        for (String commit : remoteHistory) {
            if (localHistory.contains(commit)) {
                localHistory.remove(commit);
            }
        }
        return localHistory;
    }

    /**
     * Pushes objects in a collection of outgoing commits.
     * @param localRepo local repo..
     * @param remoteRepo remote repo.
     * @param outGoingCommits outgoing commits.
     */
    private static void pushCommits(RepositoryManager localRepo,
                                    RepositoryManager remoteRepo,
                                    List<String> outGoingCommits) {
        List<GitletObject> outgoing = new ArrayList<>();

        for (String hash : outGoingCommits) {

            Commit c = localRepo.objects().getByHash(hash, Commit.class);
            List<GitletObject> blobs = c.getBlobs().values().stream().map((x)
                    -> localRepo.objects().getByHash(x, Blob.class))
                    .collect(Collectors.toList());

            outgoing.add(c);
            outgoing.addAll(blobs);
        }
        remoteRepo.objects().putAll(outgoing);
    }

    /**
     * fast forward merge in remote.
     * @param remoteRepo the remote.
     * @param remoteHeadHash head commit hash of remote repo.
     * @param localHeadHash  head commit hash of local repo
     */
    private static void fastForward(RepositoryManager remoteRepo,
                                    String remoteHeadHash,
                                    String localHeadHash) {
        if (remoteRepo.getHeadCommitHash().equals(remoteHeadHash)) {
            ResetCommand.reset(remoteRepo, localHeadHash);
        } else {
            remoteRepo.refs().getByName(remoteHeadHash, Type.Branch)
                    .setHead(localHeadHash);
        }
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
