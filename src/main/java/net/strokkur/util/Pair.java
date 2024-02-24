package net.strokkur.util;

public record Pair<T1, T2>(T1 left, T2 right) {

    @Override
    public String toString() {
        return String.format("(%s, %s)", left, right);
    }

}
