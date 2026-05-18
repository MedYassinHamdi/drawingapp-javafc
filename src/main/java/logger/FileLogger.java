package logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileLogger implements Logger {
    private String fileName = "app.log";

    @Override
    public void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
            out.println("[File] " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearLog() {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            // overwrite mode (empty file)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
