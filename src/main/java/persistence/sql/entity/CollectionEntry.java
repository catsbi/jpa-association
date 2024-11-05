package persistence.sql.entity;

import jakarta.persistence.Id;
import persistence.sql.clause.Clause;
import persistence.sql.context.KeyHolder;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.data.Status;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionEntry<T> {
    private final MetadataLoader<T> loader;
    private boolean loaded;
    private Status status;
    private List<T> entries;
    private List<T> snapshotEntries;

    public CollectionEntry(MetadataLoader<T> loader, boolean loaded, Status status, List<T> entries, List<T> snapshotEntries) {
        this.loader = loader;
        this.loaded = loaded;
        this.status = status;
        this.entries = entries;
        this.snapshotEntries = snapshotEntries;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> createSnapshot(List<T> entries, MetadataLoader<?> loader) {
        try {
            List<T> snapshotEntries = new ArrayList<>(entries.size());
            for (T entry : entries) {
                T snapshot = (T) loader.getNoArgConstructor().newInstance();
                overwritingObject(loader, entry, snapshot);
                snapshotEntries.add(snapshot);
            }

            return snapshotEntries;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create snapshot entity");
        }
    }

    private static <T> void overwritingObject(MetadataLoader<?> loader, T entry, T snapshot) throws IllegalAccessException {
        for (Field field : loader.getFieldAllByPredicate(field -> true)) {
            field.setAccessible(true);
            field.set(snapshot, field.get(entry));
        }
    }

    public List<T> getEntries() {
        return entries;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void add(T entity) {
        this.entries.add(entity);
    }

    public void synchronizingSnapshot() {
        if (snapshotEntries == null) {
            snapshotEntries = createSnapshot(entries, loader);
            return;
        }

        for (int i = 0; i < entries.size(); i++) {
            T entity = entries.get(i);
            T snapshot = snapshotEntries.get(i);

            loader.getFieldAllByPredicate(field -> !field.isAnnotationPresent(Id.class))
                    .forEach(field -> copyFieldValue(field, entity, snapshot));
        }
    }

    private void copyFieldValue(Field field, Object entity, Object origin) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);
            field.set(origin, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access to field: " + field.getName());
        }
    }

    public boolean isDirty() {
        if (isNotManagedStatus()) {
            return false;
        }

        if (!(snapshotEntries == null && entries == null) && snapshotEntries == null || entries == null) {
            return true;
        }

        for (int i = 0; i < entries.size(); i++) {
            T entity = entries.get(i);
            T snapshot = snapshotEntries.get(i);

            if (isDirty(entity, snapshot)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDirty(T entity, T snapshot) {
        if (!(snapshot == null && entity == null) && snapshot == null || entity == null) {
            return true;
        }

        List<Field> fields = loader.getFieldAllByPredicate(field -> {
            Object entityValue = Clause.extractValue(field, entity);
            Object snapshotValue = Clause.extractValue(field, snapshot);

            if (entityValue == null && snapshotValue == null) {
                return false;
            }

            if (entityValue == null || snapshotValue == null) {
                return true;
            }

            return !entityValue.equals(snapshotValue);
        });

        return !fields.isEmpty();
    }


    private boolean isNotManagedStatus() {
        return !Status.isManaged(status);
    }

    public List<T> getSnapshotEntries() {
        return Collections.unmodifiableList(snapshotEntries);
    }

    public Status getStatus() {
        return status;
    }

    public boolean isInitialize() {
        return loaded;
    }

    public void updateEntries(List<T> target) {
        this.entries = target;
        this.snapshotEntries = createSnapshot(target, loader);
        this.loaded = true;
    }
}
