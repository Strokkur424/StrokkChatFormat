package net.strokkur.util;

public record Trio<T1, T2, T3>(T1 left, T2 middle, T3 right) {

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", left, middle, right);
    }

    @Override
    public T1 left() {
        return left;
    }

    @Override
    public T2 middle() {
        return middle;
    }

    @Override
    public T3 right() {
        return right;
    }
}
