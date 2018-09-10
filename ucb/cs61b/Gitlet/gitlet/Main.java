package gitlet;

import java.nio.file.Paths;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Weifeng Dong
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        RepositoryManager repo = new RepositoryManager(Paths.get(""));

        try {
            CommandManager.accept(repo, args);
        } catch (GitletException e) {
            System.out.println(e.getMessage());
        }

        if (repo.isOpen()) {
            repo.close();
        }
    }
}
