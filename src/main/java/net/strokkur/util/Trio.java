package net.strokkur.util;

public record Trio<T1, T2, T3>(T1 left, T2 middle, T3 right) {

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", left, middle, right);
    }
}
