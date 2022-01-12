package xienaoban.minecraft.bole.util;

@FunctionalInterface
public interface TreeNodeExecutor<E> {
    static <E> TreeNodeExecutor<E> empty() {
        return (cur, depth) -> true;
    }

    boolean execute(E cur, int depth);
}
