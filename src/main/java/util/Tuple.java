package util;

public class Tuple<T1, T2> {
    private T1 first;
    private T2 second;

    public T1 getFirst() {
        return first;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }

    private Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public static <T1, T2> Tuple<T1, T2> of(T1 first, T2 second) {
        return new Tuple<>(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }
}
