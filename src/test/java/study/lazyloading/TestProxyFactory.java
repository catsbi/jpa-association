package study.lazyloading;

import persistence.proxy.ProxyFactory;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.context.KeyHolder;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;

public class TestProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T, C extends Collection<T>> C createProxyCollection(Object foreignKey, Class<?> foreignType, Class<T> targetClass) {
        EntityLoader<T> loader = EntityLoaderFactory.getInstance().getLoader(targetClass);

        return (C) Proxy.newProxyInstance(
                List.class.getClassLoader(),
                new Class[]{List.class},
                new LazyLoadingHandler<>(new KeyHolder(foreignType, foreignKey), loader)
        );
    }
}
