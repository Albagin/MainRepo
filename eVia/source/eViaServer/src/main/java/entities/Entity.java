package entities;

public interface Entity {
    boolean equals(Object o);
    int hashCode();
    String toString();
    Integer getId();
}
