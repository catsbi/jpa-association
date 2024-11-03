package persistence.sql.ddl.impl;

import java.lang.reflect.Field;

public class JoinTargetDefinition {
    private final Class<?> joinedEntity;
    private final Field joinedField;
    private final Class<?> targetEntity;

    public JoinTargetDefinition(Class<?> joinedEntity, Field joinedField, Class<?> targetEntity) {
        this.joinedEntity = joinedEntity;
        this.joinedField = joinedField;
        this.targetEntity = targetEntity;
    }

    public Class<?> getJoinedEntity() {
        return joinedEntity;
    }

    public Field getJoinedField() {
        return joinedField;
    }

    public Class<?> getTargetEntity() {
        return targetEntity;
    }

    @Override
    public String toString() {
        return "JoinTargetDefinition{" +
                "joinedEntity=" + joinedEntity +
                ", joinedField=" + joinedField +
                ", targetEntity=" + targetEntity +
                '}';
    }
}
