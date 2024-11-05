package persistence.proxy.impl;

import persistence.proxy.ProxyFactory;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Proxy;
import java.util.List;

public class JdkProxyFactory implements ProxyFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T, C extends List<T>> C createProxyCollection(Object foreignKey,
                                                          Class<?> foreignType,
                                                          Class<T> targetClass,
                                                          PersistenceContext persistenceContext) {
        EntityLoader<T> loader = EntityLoaderFactory.getInstance().getLoader(targetClass);

        return (C) Proxy.newProxyInstance(
                List.class.getClassLoader(),
                new Class[]{List.class},
                new LazyLoadingHandler<>(new KeyHolder(foreignType, foreignKey), loader, persistenceContext)
        );
    }
}
