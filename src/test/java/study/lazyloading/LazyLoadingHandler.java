package study.lazyloading;

import persistence.sql.EntityLoaderFactory;
import persistence.sql.context.KeyHolder;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class LazyLoadingHandler<T> implements InvocationHandler {
    private static final Logger logger = Logger.getLogger(LazyLoadingHandler.class.getName());

    private final KeyHolder parentKeyHolder;
    private final EntityLoader<T> entityLoader;
    private List<T> target;

    public LazyLoadingHandler(KeyHolder parentKeyHolder, EntityLoader<T> entityLoader) {
        this.parentKeyHolder = parentKeyHolder;
        this.entityLoader = entityLoader;
    }

    public static <T> LazyLoadingHandler<T> newInstance(Object foreignKey, Class<?> foreignType, Class<T> targetClass) {
        EntityLoader<T> entityLoader = EntityLoaderFactory.getInstance().getLoader(targetClass);

        return new LazyLoadingHandler<>(new KeyHolder(foreignType, foreignKey), entityLoader);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("invoke method name: %s, args: %s".formatted(method.getName(), Arrays.toString(args)));
        if (target == null) {
            realize();
        }

        return method.invoke(target, args);
    }

    private void realize() {
        EntityLoader<?> targetLoader = EntityLoaderFactory.getInstance().getLoader(parentKeyHolder.entityType());
        target = entityLoader.loadAllByForeignKey(parentKeyHolder.key(), targetLoader.getMetadataLoader());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LazyLoadingHandler that)) {
            return false;
        }
        return Objects.equals(parentKeyHolder, that.parentKeyHolder) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentKeyHolder, target);
    }
}
