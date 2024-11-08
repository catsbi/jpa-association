package persistence.sql.dml.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import persistence.sql.clause.InsertColumnValueClause;
import persistence.sql.clause.WhereConditionalClause;
import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.data.QueryType;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.fixture.TestOrder;
import persistence.sql.fixture.TestOrderItem;
import persistence.sql.fixture.TestPerson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("InsertQueryBuilder 테스트")
class InsertQueryBuilderTest {
    private final InsertQueryBuilder builder = new InsertQueryBuilder();
    private final MetadataLoader<TestPerson> loader = new SimpleMetadataLoader<>(TestPerson.class);

    @Test
    @DisplayName("build 함수는 유효한 조건절을 전달할 경우 쿼리를 생성한다.")
    void testInsertQueryBuild() {
        // given
        TestPerson person = new TestPerson("catsbi", 55, "catsbi@naver.com", 123);
        InsertColumnValueClause clause = InsertColumnValueClause.newInstance(person, CamelToSnakeConverter.getInstance());

        // when
        String query = builder.build(loader, clause);

        // then
        assertThat(query).isEqualTo("INSERT INTO users (nick_name, old, email) VALUES ('catsbi', 55, 'catsbi@naver.com')");
    }


    @Test
    @DisplayName("build 함수는 해당 엔티티를 참조하는 엔티티가 있는경우 해당 참조하는 엔티티의 외래키를 추가한 insert query를 생성한다.")
    void testInsertQueryBuildWithParentEntity() {
        // given
        TestOrder testOrder = new TestOrder("order1");
        TestOrderItem apple = new TestOrderItem("apple", 10);
        TestOrderItem grape = new TestOrderItem("grape", 20);
        testOrder.addOrderItem(apple);
        testOrder.addOrderItem(grape);

        InsertColumnValueClause clause = InsertColumnValueClause.newInstance(apple, testOrder, CamelToSnakeConverter.getInstance());

        System.out.println("clause = " + clause.clause());

    }

    @Test
    @DisplayName("queryType 함수는 INSERT를 반환한다.")
    void testQueryType() {
        // when
        var queryType = builder.queryType();

        // then
        assertThat(queryType).isEqualTo(QueryType.INSERT);
    }

    @ParameterizedTest
    @CsvSource({"DELETE, false", "SELECT, false", "UPDATE, false", "INSERT, true"})
    @DisplayName("supported 함수는 매개변수가 INSERT일 경우 true를 반환한다.")
    void testSupported(QueryType queryType, boolean expected) {
        // when
        var supported = builder.supported(queryType);

        // then
        assertThat(supported).isEqualTo(expected);
    }

    @Test
    @DisplayName("build 함수는 조건절 매개변수가 없을 경우 쿼리를 생성하지 않는다.")
    void testBuildWithoutClause() {
        // when, then
        assertThatThrownBy(() -> builder.build(loader))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Conditional clauses are not supported for insert query");
    }

    @Test
    @DisplayName("build 함수는 유효하지 않은 조건절을 전달할 경우 예외를 던진다.")
    void testBuildWithClause() {
        WhereConditionalClause invalidClause = WhereConditionalClause.builder().column("id").eq("1");

        // when, then
        assertThatThrownBy(() -> builder.build(loader, invalidClause))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("InsertColumnValueClause is required for insert query");
    }

}
