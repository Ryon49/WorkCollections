package gitlet;

/** Command interface.
 *  @author Weifeng Dong
 */
public interface Command {
    /**
     * execute command.
     * @param repo current repository.
     * @param args operand inputs.
     */
    void execute(RepositoryManager repo, String[] args);

    /**
     * @return true if current command requires repo to be open.
     */
    boolean requiresRepo();

    /**
     * @param args input.
     * @return return true if length of operand is correct.
     */
    boolean checkOperands(String[] args);
}
