package event.interfaces;
@FunctionalInterface
public interface Event<T> {
    void invoke(T arg);
}