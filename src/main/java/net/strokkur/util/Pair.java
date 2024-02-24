package net.strokkur.util;

public class Pair<T1, T2> {

    public T1 left;
    public T2 right;

    public Pair(T1 left, T2 right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", left, right);
    }

}
