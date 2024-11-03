package persistence.sql.ddl.impl;

import java.lang.reflect.Field;

public class JoinTargetDefinition {
    private final Object joinedEntity;
    private final Field joinedField;
    private final Object targetEntity;

    public JoinTargetDefinition(Object joinedEntity, Field joinedField, Object targetEntity) {
        this.joinedEntity = joinedEntity;
        this.joinedField = joinedField;
        this.targetEntity = targetEntity;
    }

    public Object getJoinedEntity() {
        return joinedEntity;
    }

    public Field getJoinedField() {
        return joinedField;
    }

    public Object getTargetEntity() {
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
