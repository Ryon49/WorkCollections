package gitlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import gitlet.Reference.Type;
/**
 * Command for merging two branches.
 *
 * @author Weifeng Dong
 */
public class MergeCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {
        merge(repo, args[0]);
    }

    /**
     * prepare merging.
     * @param repo current repo.
     * @param branchName given branch name.
     */
    public static void merge(RepositoryManager repo, String branchName) {
        if (repo.objects().info().changed()) {
            throw new GitletException(
                    "You have uncommitted changes.");
        }
        if (!repo.refs().contains(branchName, Type.Branch)) {
            throw new GitletException(
                    " A branch with that name does not exist.");
        }
        if (branchName.equals(repo.refs().getCurrentBranch().name())) {
            throw new GitletException(
                    "Cannot merging a branch with itself.");
        }

        String headCommitHash = repo.getHeadCommitHash();
        Reference targetBranch = repo.refs().getByName(branchName, Type.Branch);
        String targetCommitHash = targetBranch.target();

        String splitPoint = getSplitPoint(repo,
                headCommitHash, targetCommitHash);
        if (splitPoint.equals(headCommitHash)) {
            fastForward(repo, splitPoint);
        }
        if (splitPoint.equals(targetCommitHash)) {
            throw new GitletException(
                    "Given branch is an ancestor of the current branch.");
        }

        Commit head = repo.getByHash(headCommitHash, Commit.class);
        Commit split = repo.getByHash(splitPoint, Commit.class);
        Commit target = repo.getByHash(targetCommitHash, Commit.class);

        boolean hasConflict = merging(repo, head, target, split);

        addMergedCommit(repo, headCommitHash, targetBranch, targetCommitHash);

        if (hasConflict) {
            throw new GitletException("Encountered a merging conflict.");
        }
    }



    /**
     * create a new commit after merging is finished.
     * @param repo current repository.
     * @param headCommitHash one of parent hash of commit.
     * @param targetBranch branch being merged.
     * @param targetCommitHash one of parent hash of commit.
     */
    private static void addMergedCommit(RepositoryManager repo,
                                        String headCommitHash,
                                        Reference targetBranch,
                                        String targetCommitHash) {
        String msg = new StringJoiner(" ", "", ".")
                .add("Merged")
                .add(targetBranch.name())
                .add("into")
                .add(repo.refs().getCurrentBranch().name())
                .toString();

        Commit newCommit = new Commit(msg,
                headCommitHash, targetCommitHash,
                Instant.now(),
                repo.objects().info().getBlobs());
        CommitCommand.addCommit(repo, newCommit);
    }

    /**
     * perform merging action.
     * @param repo current repository.
     * @param head head commit.
     * @param given given commit.
     * @param split split commit.
     * @return true if conflicts happens
     */
    public static boolean merging(RepositoryManager repo, Commit head,
                                  Commit given, Commit split) {
        List<String> safeCheckout = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();
        List<String> inConflict = new ArrayList<>();

        given.forEach((fileName, targetHash) -> {
                String headHash = head.get(fileName);
                String splitHash = split.get(fileName);

                if (splitHash == null) {
                    if (headHash == null) {
                        safeCheckout.add(fileName);
                    } else if (!headHash.equals(targetHash)) {
                        inConflict.add(fileName);
                    }
                } else if (!targetHash.equals(headHash)) {
                    if (headHash == null) {
                        if (!targetHash.equals(splitHash)) {
                            inConflict.add(fileName);
                        }
                    } else if (!targetHash.equals(splitHash)
                            || !targetHash.equals(headHash)) {
                        inConflict.add(fileName);
                    }
                }
            });

        head.forEach((fileName, headHash) -> {
                String splitHash = split.get(fileName);
                String targetHash = given.get(fileName);
                if (splitHash != null && targetHash == null) {
                    if (headHash.equals(splitHash)) {
                        toRemove.add(fileName);
                    } else {
                        inConflict.add(fileName);
                    }
                }
            });

        mergeCheckout(repo, given, safeCheckout);
        mergeRemove(repo, head, toRemove);
        mergeConflict(repo, head, given, inConflict);

        return !inConflict.isEmpty();

    }

    /**
     * Checks out all files (safe).
     * @param repo current repository.
     * @param target commit from which to checkout.
     * @param checkout files to checkout.
     */
    private static void mergeCheckout(RepositoryManager repo,
                                      Commit target, List<String> checkout) {
        Path workDir = repo.getWorkDir();
        State info = repo.objects().info();

        for (String fileName : checkout) {
            if (Files.exists(workDir.resolve(fileName))
                    && !info.getBlobs().containsKey(fileName)) {
                throw new GitletException("There is an untracked "
                        + "file in the way; delete it or add it first.");
            }
        }

        checkout.forEach(x -> repo.checkoutFile(target, x, true));
    }
    /**
     * Removes all files from the repository which the mergeCompare deems
     * removable.
     * @param repo current repository.
     * @param head commit which file removes from.
     * @param toRemove files to remove.
     */
    private static void mergeRemove(RepositoryManager repo,
                                    Commit head, List<String> toRemove) {
        toRemove.forEach(x -> RemoveCommand.remove(repo, x, head));
    }

    /**
     * Merges the conflicts files.
     * @param repo current repository.
     * @param head head commit.
     * @param target target commit.
     * @param inConflict files in conflict.
     */
    private static void mergeConflict(RepositoryManager repo, Commit head,
                                      Commit target, List<String> inConflict) {

        State info = repo.objects().info();
        for (String file : inConflict) {
            Path filePath = repo.getWorkDir().resolve(file);
            try {
                byte[] bytes = "<<<<<<< HEAD\n".getBytes();
                Files.write(filePath, bytes);

                if (head.containsFile(file)) {
                    Blob headVersion = repo.getByHash(
                            head.getBlobs().get(file), Blob.class);

                    Files.write(filePath, headVersion.getContents(),
                            StandardOpenOption.APPEND);
                }

                Files.write(filePath, "=======\n".getBytes(),
                        StandardOpenOption.APPEND);

                if (target.getBlobs().containsKey(file)) {
                    Blob targetVersion = repo.getByHash(
                            target.getBlobs().get(file), Blob.class);

                    Files.write(filePath, targetVersion.getContents(),
                            StandardOpenOption.APPEND);
                }

                Files.write(filePath, ">>>>>>>\n".getBytes(),
                        StandardOpenOption.APPEND);


                Blob newBlob = new Blob(Utils.readContents(filePath));
                AddCommand.add(repo, file, newBlob);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * fast forward merging.
     *
     * @param repo   current repository.
     * @param target target commit.
     */
    private static void fastForward(RepositoryManager repo, String target) {
        repo.checkoutCommit(
                repo.objects().getByHash(target, Commit.class),
                true);
        System.out.println("Current branch fast-forwarded. ");
    }

    /**
     * find the closest common ancestor of two commits,
     * method guaranteed not null.
     *
     * @param repo  current repository.
     * @param head  input.
     * @param other input.
     * @return latest common commit hash
     */
    public static String getSplitPoint(RepositoryManager repo,
                                       String head, String other) {
        if (head.equals(other)) {
            return head;
        }
        List<String> headHistory = repo.getCommitHistory(head);
        List<String> otherHistory = repo.getCommitHistory(other);
        headHistory.retainAll(otherHistory);

        return headHistory.get(0);
    }

    @Override
    public boolean requiresRepo() {
        return false;
    }

    @Override
    public boolean checkOperands(String[] args) {
        return args.length == 1;
    }
}
