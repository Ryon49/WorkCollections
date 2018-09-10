package gitlet;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Collection of Commands.
 * @author Weifeng Dong
 */
public class CommandManager {

    /**
     * store commands.
     */
    private static HashMap<String, Command> _commands
            = new HashMap<>();

    /**
     * default constructor, unable to initialize.
     */
    private CommandManager() {
    }

    static {
        _commands.put("init", new InitCommand());
        _commands.put("add", new AddCommand());
        _commands.put("commit", new CommitCommand());
        _commands.put("rm", new RemoveCommand());
        _commands.put("log", new LogCommand());
        _commands.put("global-log", new GlobalLogCommand());
        _commands.put("find", new FindCommand());
        _commands.put("status", new StatusCommand());
        _commands.put("branch", new BranchCommand());
        _commands.put("rm-branch", new RemoveBranchCommand());
        _commands.put("checkout", new CheckoutCommand());
        _commands.put("reset", new ResetCommand());
        _commands.put("merge", new MergeCommand());

        _commands.put("add-remote", new AddRemoteCommand());
        _commands.put("fetch", new FetchCommand());
        _commands.put("rm-remote", new RemoveRemoteCommand());
        _commands.put("push", new PushCommand());
        _commands.put("pull", new PullCommand());
    }

    /**
     * accept repo and args to perform command.
     *
     * @param repo current repository.
     * @param args {command, operand}
     */
    public static void accept(RepositoryManager repo, String[] args) {
        if (args == null || args.length == 0) {
            throw new GitletException("Please enter a command.");
        }

        Command command = _commands.get(args[0]);

        if (command == null) {
            throw new GitletException(
                    "No command with that name exists.");
        }

        String[] operands = Arrays.copyOfRange(args, 1, args.length);
        if (!command.checkOperands(operands)) {
            throw new GitletException("Incorrect operands.");
        }

        if (command.requiresRepo() && !repo.isOpen()) {
            throw new GitletException(
                    "Not in an initialized gitlet directory.");
        }

        command.execute(repo, operands);
    }
}

