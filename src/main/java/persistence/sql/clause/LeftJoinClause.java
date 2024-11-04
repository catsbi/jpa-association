package persistence.sql.clause;

import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.common.util.NameConverter;
import persistence.sql.data.ClauseType;
import persistence.sql.ddl.impl.JoinTargetDefinition;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.holder.JoinTargetHolder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record LeftJoinClause(String table, String leftColumn, String rightColumn, String columns) implements JoinClause {

    /**
     * 두 테이블을 조회하는 LEFT JOIN 절을 생성한다.
     *
     * @param origin 원 테이블
     * @param target join 대상 테이블
     * @return
     */
    public static LeftJoinClause of(Class<?> origin, Class<?> target) {
        NameConverter converter = CamelToSnakeConverter.getInstance();
        MetadataLoader<?> originMeta = new SimpleMetadataLoader<>(origin);
        MetadataLoader<?> targetMeta = new SimpleMetadataLoader<>(target);

        Field originKey = originMeta.getPrimaryKeyField();
        Field targetKey = targetMeta.getPrimaryKeyField();

        String leftColumn = originMeta.getColumnName(originKey, converter);
        String rightColumn = targetMeta.getColumnName(targetKey, converter);

        List<String> columns = targetMeta.getFieldAllByPredicate(field -> true).stream()
                .map(field -> JoinClause.combineAlias(targetMeta.getTableAlias(), targetMeta.getColumnName(field, converter)))
                .collect(Collectors.toCollection(ArrayList::new));

        return new LeftJoinClause(
                targetMeta.getTableName() + " " + targetMeta.getTableAlias(),
                JoinClause.combineAlias(originMeta.getTableAlias(), leftColumn),
                JoinClause.combineAlias(targetMeta.getTableAlias(), rightColumn),
                String.join(", ", columns)
        );
    }

    @Override
    public boolean supported(ClauseType clauseType) {
        return clauseType == ClauseType.LEFT_JOIN;
    }
}
