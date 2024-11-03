package persistence.sql.holder;

import persistence.sql.ddl.impl.JoinTargetDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JoinTargetHolder {
    private static final JoinTargetHolder INSTANCE = new JoinTargetHolder();

    private static final Map<Class<?>, Set<JoinTargetDefinition>> CONTEXT = new HashMap<>();

    private JoinTargetHolder() {
    }

    public static JoinTargetHolder getInstance() {
        return INSTANCE;
    }

    public void add(Class<?> clazz, JoinTargetDefinition definitions) {
        CONTEXT.computeIfAbsent(clazz, k -> Set.of()).add(definitions);
    }

    public void add(JoinTargetDefinition definition) {
        add(definition.getTargetEntity(), definition);
    }

    public Set<JoinTargetDefinition> get(Class<?> clazz) {
        if (!CONTEXT.containsKey(clazz)) {
            throw new IllegalArgumentException("No join target definition found for " + clazz);
        }

        return CONTEXT.get(clazz);
    }
}
