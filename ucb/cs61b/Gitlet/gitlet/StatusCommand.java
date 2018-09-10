package gitlet;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Command for printing current status.
 * @author Weifeng Dong
 */
public class StatusCommand implements Command {
    @Override
    public void execute(RepositoryManager repo, String[] args) {

        System.out.println("=== Branches ===");
        ReferencesManager refs = repo.refs();
        refs.tracked().stream()
                .sorted()
                .forEach(refName -> {
                        if (refName.equals(refs.getCurrentBranch().name())) {
                            System.out.print('*');
                        }
                        System.out.println(refName);
                    });

        State info = repo.objects().info();

        System.out.println("\n=== Staged Files ===");
        info.getStaged().forEach((filename, hash)
                -> System.out.println(filename));

        System.out.println("\n=== Removed Files ===");
        info.getRemoved().forEach((filename, hash)
                -> System.out.println(filename));

        diff(info, Paths.get("").toAbsolutePath());
    }

    /**
     * find differenet between current directory and current commit.
     * @param index blob index.
     * @param base work directory.
     */
    private void diff(State index, Path base) {
        HashMap<String, String> curr = new HashMap<>();

        try {
            for (Path path : Files.newDirectoryStream(base)) {
                if (!Files.isDirectory(path)) {
                    String fileName = path.getFileName().toString();

                    Blob blob = new Blob(Files.readAllBytes(path));
                    curr.put(fileName, blob.getHash());
                }
            }

            List<String> untracked = new ArrayList<>();
            List<String> unStaged = new ArrayList<>();

            curr.forEach((filename, hash) -> {
                    if (!index.getBlobs().containsKey(filename)) {
                        untracked.add(filename);
                    } else if (!index.getBlobs().get(filename).equals(hash)) {
                        unStaged.add(filename + " (modified)");
                    }
                });

            index.getBlobs().forEach((name, hash) -> {
                    if (!curr.containsKey(name)) {
                        unStaged.add(name + " (deleted)");
                    }
                });

            System.out.println("\n=== Modifications Not Staged For Commit ===");
            unStaged.stream().sorted(String::compareTo)
                    .forEach(System.out::println);

            System.out.println("\n=== Untracked Files ===");
            untracked.stream().sorted(String::compareTo)
                    .forEach(System.out::println);

        } catch (IOException | DirectoryIteratorException e) {
            throw new RuntimeException(e.getMessage());
        }
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
