package xienaoban.minecraft.bole.util;

@FunctionalInterface
public interface TreeNodeExecutor<E> {
    boolean execute(E cur, int depth);
}
