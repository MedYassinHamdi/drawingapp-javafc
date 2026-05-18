package logger;

public class LoggerContext {
    private Logger strategy;

    public LoggerContext(Logger strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(Logger strategy) {
        this.strategy = strategy;
    }
    public Logger getStrategy() {
        return strategy;
    }

    public void log(String message) {
        strategy.log(message);
    }
}
