package utility;

public class LogMessageBuilder {
    private final StringBuilder builder = new StringBuilder();

    LogMessageBuilder(Object[] objects) {
        for (Object o : objects) {
            this.append(o);
        }
    }

    void append (Object toAppend) {
        if (builder.toString().isEmpty()) {
            builder.append(toAppend);
        }
        else {
            builder.append(" ").append(toAppend);
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
