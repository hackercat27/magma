package ca.hackercat.logging;

public final class ANSI {
    private ANSI() {}

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";

    public static final String FOREGROUND_BLACK = "\u001B[30m";
    public static final String FOREGROUND_RED = "\u001B[31m";
    public static final String FOREGROUND_GREEN = "\u001B[32m";
    public static final String FOREGROUND_YELLOW = "\u001B[33m";
    public static final String FOREGROUND_BLUE = "\u001B[34m";
    public static final String FOREGROUND_MAGENTA = "\u001B[35m";
    public static final String FOREGROUND_CYAN = "\u001B[36m";
    public static final String FOREGROUND_LIGHT_GRAY = "\u001B[37m";
    public static final String FOREGROUND_DARK_GRAY = "\u001B[90m";
    public static final String FOREGROUND_LIGHT_RED = "\u001B[91m";
    public static final String FOREGROUND_LIGHT_GREEN = "\u001B[92m";
    public static final String FOREGROUND_LIGHT_YELLOW = "\u001B[93m";
    public static final String FOREGROUND_LIGHT_BLUE = "\u001B[94m";
    public static final String FOREGROUND_LIGHT_MAGENTA = "\u001B[95m";
    public static final String FOREGROUND_LIGHT_CYAN = "\u001B[96m";
    public static final String FOREGROUND_WHITE = "\u001B[97m";

    public static final String BACKGROUND_BLACK = "\u001B[40m";
    public static final String BACKGROUND_RED = "\u001B[41m";
    public static final String BACKGROUND_GREEN = "\u001B[42m";
    public static final String BACKGROUND_YELLOW = "\u001B[43m";
    public static final String BACKGROUND_BLUE = "\u001B[44m";
    public static final String BACKGROUND_MAGENTA = "\u001B[45m";
    public static final String BACKGROUND_CYAN = "\u001B[46m";
    public static final String BACKGROUND_LIGHT_GRAY = "\u001B[47m";
    public static final String BACKGROUND_DARK_GRAY = "\u001B[100m";
    public static final String BACKGROUND_LIGHT_RED = "\u001B[101m";
    public static final String BACKGROUND_LIGHT_GREEN = "\u001B[102m";
    public static final String BACKGROUND_LIGHT_YELLOW = "\u001B[103m";
    public static final String BACKGROUND_LIGHT_BLUE = "\u001B[104m";
    public static final String BACKGROUND_LIGHT_MAGENTA = "\u001B[105m";
    public static final String BACKGROUND_LIGHT_CYAN = "\u001B[106m";
    public static final String BACKGROUND_WHITE = "\u001B[107m";
}
