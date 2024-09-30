package event;
@FunctionalInterface
public interface Event<T> {
    void invoke(T arg);
}
