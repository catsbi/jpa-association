package persistence.sql.context;

import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.CollectionEntry;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;

import java.util.AbstractCollection;

public interface PersistenceContext {

    <T> EntityEntry addEntry(T entity, Status status, EntityPersister entityPersister);

    <T> EntityEntry addLoadingEntry(Object primaryKey, Class<T> returnType);

    <T, ID> EntityEntry getEntry(Class<T> entityType, ID id);

    <T> CollectionEntry<T> getCollectionEntry(AbstractCollection<?> abstractCollection);

    <T> CollectionEntry<T> addCollectionEntry(AbstractCollection<?> abstractCollection, CollectionEntry<T> collectionEntry);

    <T, ID> void deleteEntry(T entity, ID id);

    void cleanup();

    void dirtyCheck(EntityPersister persister);
}
