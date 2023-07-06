package ca.hackercat.logging;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    public static final Logger LOGGER = new Logger(System.out);

    private PrintStream out;

    private boolean supportsANSI = true;
    private boolean printNewLines = true;

    public Logger(String name) {
        this.out = LOGGER.out;
    }
    public Logger(PrintStream out) {
        this.out = out;
    }

    private enum Level {
        INFO,
        WARN,
        ERROR
    }

    public void setPrintNewLines(boolean printNewLines) {
        this.printNewLines = printNewLines;
    }
    public void setSupportsANSI(boolean supportsANSI) {
        this.supportsANSI = supportsANSI;
    }

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
        switch (level) {
            case INFO ->
                    out.println("[" + getTime() + "] " +
                            "[" + ANSI.FOREGROUND_LIGHT_BLUE + ANSI.BOLD + "info" + ANSI.RESET + "/" + thread + "] " +
                            message + ANSI.RESET);
            case WARN ->
                    out.println("[" + getTime() + "] " +
                            "[" + ANSI.FOREGROUND_LIGHT_YELLOW + ANSI.BOLD + "WARN" + ANSI.RESET + "/" + thread + "] " +
                            message + ANSI.RESET);
            case ERROR ->
                    out.println(ANSI.FOREGROUND_LIGHT_RED + "[" + getTime() + "] " +
                            "[" + ANSI.BOLD + "ERROR" + ANSI.RESET + ANSI.FOREGROUND_LIGHT_RED + "/" + thread + "] " +
                            message + ANSI.RESET);
        }
        String str = ANSI.FOREGROUND_LIGHT_RED + "[" + getTime() + "] " + "[" + ANSI.BOLD + "ERROR" + ANSI.RESET + ANSI.FOREGROUND_LIGHT_RED + "/" + thread + "] " + message + ANSI.RESET;
    }
    private void printBare(Level level, String message) {
        String thread = Thread.currentThread().getName();
        switch (level) {
            case INFO ->
                    out.println("[" + getTime() + "] " +
                            "[info/" + thread + "] " +
                            message);
            case WARN ->
                    out.println("[" + getTime() + "] " +
                            "[WARN/" + thread + "] " +
                            message);
            case ERROR ->
                    out.println("[" + getTime() + "] " +
                            "[ERROR/" + thread + "] " +
                            message);
        }
    }

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String getTime() {
        return dtf.format(LocalDateTime.now());
    }
}
