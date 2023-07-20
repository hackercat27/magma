package ca.hackercat.logging;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    private PrintStream out;

//    private boolean ansi = true;
    private String className;

    private static final String INFO_COLOR = "\u001b[38;2;22;151;218m"; // light blue
    private static final String INFO_TEXT = "";
    private static final String WARN_COLOR = "\u001b[38;2;255;255;0m"; // yellow
    private static final String WARN_TEXT = "";
    private static final String ERROR_COLOR = "\u001b[1;38;2;255;0;0m"; // bold red
    private static final String ERROR_TEXT = "\u001b[38;2;255;0;0m"; // red

    private static final String RESET_COLOR = "\u001b[0m";

    private Logger() {}

    private Logger(String className) {
        this.className = className;
        this.out = System.out;
    }
//    private Logger(Class<T> clazz, PrintStream out) {
//        this.clazz = clazz;
//        this.out = out;
//    }

    public static <T> Logger get(Class<T> clazz) {
        return new Logger(clazz.getSimpleName());
    }

    private enum Level {
        INFO,
        WARN,
        ERROR
    }

//    public void setPrintNewLines(boolean printNewLines) {
//        this.printNewLines = printNewLines;
//    }
//    public void setSupportsANSI(boolean supportsANSI) {
//        this.supportsANSI = supportsANSI;
//    }

    public void log(Object o) {
        log(o.toString());
    }
    public void log(String msg) {
        print(Level.INFO, msg);
    }
    public void warn(Object o) {
        warn(o.toString());
    }
    public void warn(String msg) {
        print(Level.WARN, msg);
    }
    public void error(Object o) {
        error(o.toString());
    }
    public void error(String msg) {
        print(Level.ERROR, msg);
    }

    private void print(Level level, String message) {
        String thread = Thread.currentThread().getName();
        String name = className;

        String info = name.isBlank()? thread + "/Anonymous Class" : thread + "/" + name;

        String logPrefix = "";

        switch (level) {
            case INFO -> logPrefix = "[" + getTime() + "] [INFO/" + info + "] ";
            case WARN -> logPrefix = "[" + getTime() + "] [WARN/" + info + "] ";
            case ERROR -> logPrefix = "[" + getTime() + "] [ERROR/" + info + "] ";
        }

        if (message.contains("\n")) {
            StringBuilder replacement = new StringBuilder();
            replacement.append("\n");
            for (int i = 0, length = logPrefix.length(); i < length; i++) {
                replacement.append(" ");
            }
            message = message.replaceAll("\n", replacement.toString());
        }

        switch (level) {
            case INFO ->
                    out.println(INFO_TEXT + "[" + getTime() + "] " +
                            "[" + RESET_COLOR + INFO_COLOR + "INFO" + RESET_COLOR + INFO_TEXT + "/" + info + "] " +
                            message + RESET_COLOR);
            case WARN ->
                    out.println(WARN_TEXT + "[" + getTime() + "] " +
                            "[" + RESET_COLOR + WARN_COLOR + "WARN" + RESET_COLOR + WARN_TEXT + "/" + info + "] " +
                            message + RESET_COLOR);
            case ERROR ->
                    out.println(ERROR_TEXT + "[" + getTime() + "] " +
                            "[" + RESET_COLOR + ERROR_COLOR + "ERROR" + RESET_COLOR + ERROR_TEXT + "/" + info + "] " +
                            message + RESET_COLOR);
        }
    }

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String getTime() {
        return dtf.format(LocalDateTime.now());
    }
}
