package net.strokkur.util;

public class Pair<T1, T2> {

    T1 left;
    T2 right;

    public Pair(T1 left, T2 right) {
        this.left = left;
        this.right = right;
    }

    public T1 getLeft() {
        return left;
    }
    public T2 getRight() {
        return right;
    }

    public void setLeft(T1 n) {
        left = n;
    }
    public void setRight(T2 m) {
        right = m;
    }

}
