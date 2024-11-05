package study.lazyloading;

import persistence.proxy.ProxyFactory;
import persistence.proxy.impl.LazyLoadingHandler;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Proxy;
import java.util.Collection;

public class TestProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T, C extends Collection<T>> C createProxyCollection(Object foreignKey,
                                                                Class<?> foreignType,
                                                                Class<T> targetClass,
                                                                PersistenceContext persistenceContext) {
        EntityLoader<T> loader = EntityLoaderFactory.getInstance().getLoader(targetClass);

        return (C) Proxy.newProxyInstance(
                Collection.class.getClassLoader(),
                new Class[]{Collection.class},
                new LazyLoadingHandler<>(new KeyHolder(foreignType, foreignKey), loader, persistenceContext)
        );
    }
}
