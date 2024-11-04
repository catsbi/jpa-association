package persistence.sql.clause;

import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.common.util.NameConverter;
import persistence.sql.data.ClauseType;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;

import java.lang.reflect.Field;

public record LeftJoinClause(String table, String leftColumn, String rightColumn) implements JoinClause {

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

        return new LeftJoinClause(
                targetMeta.getTableName() + " " + targetMeta.getTableAlias(),
                JoinClause.combineAlias(originMeta.getTableAlias(), leftColumn),
                JoinClause.combineAlias(targetMeta.getTableAlias(), rightColumn)
        );
    }

    @Override
    public boolean supported(ClauseType clauseType) {
        return clauseType == ClauseType.LEFT_JOIN;
    }
}
