package net.strokkur.util;

public class Trio<T1, T2, T3> {

    public T1 left;
    public T2 middle;
    public  T3 right;

    public Trio(T1 left, T2 middle, T3 right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", left, middle, right);
    }
}
