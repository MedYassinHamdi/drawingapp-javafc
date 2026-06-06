package command;

public interface DrawCommand {
    void execute();
    void undo();
}