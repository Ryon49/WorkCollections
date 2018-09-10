package gitlet;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Command for adding file into staging area.
 * @author Weifeng Dong
 */
public class AddCommand implements Command {

    @Override
    public void execute(RepositoryManager repo, String[] args) {

        String fileName = args[0];
        Path target = repo.getWorkDir().resolve(fileName);

        if (!Files.exists(target)) {
            throw new GitletException("File does not exist.");
        }
        if (Files.isDirectory(target)) {
            throw new GitletException("Cannot add a directory.");
        }

        Blob fileBlob = new Blob(Utils.readContents(target));
        add(repo, fileName, fileBlob);
    }

    /**
     * add a new blob to repo.
     * @param repo current repository.
     * @param fileName fileName.
     * @param fileBlob blob.
     */
    public static void add(RepositoryManager repo,
                           String fileName, Blob fileBlob) {
        repo.objects().put(fileBlob.getHash(), fileBlob);
        repo.objects().info().add(fileName, fileBlob.getHash());
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
