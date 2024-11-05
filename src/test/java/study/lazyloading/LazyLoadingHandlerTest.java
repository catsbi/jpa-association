package study.lazyloading;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.config.TestPersistenceConfig;
import persistence.proxy.ProxyFactory;
import persistence.sql.dml.Database;
import persistence.sql.dml.TestEntityInitialize;
import persistence.sql.fixture.TestOrder;
import persistence.sql.fixture.TestOrderItem;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("LazyLoadingHandler 테스트")
class LazyLoadingHandlerTest extends TestEntityInitialize {
    private final ProxyFactory proxyFactory = new TestProxyFactory();

    @BeforeEach
    void setup() throws SQLException {
        TestPersistenceConfig config = TestPersistenceConfig.getInstance();
        Database database = config.database();

        database.executeUpdate("INSERT INTO orders (order_number) VALUES ('1')");
        database.executeUpdate("INSERT INTO order_items (product, quantity, order_id) VALUES ('apple', 10, 1)");
        database.executeUpdate("INSERT INTO order_items (product, quantity, order_id) VALUES ('cherry', 20, 1)");
    }

    @Test
    @DisplayName("생성자를 통해 프록시 객체를 생성할 수 있다.")
    void constructor() {

        List<TestOrderItem> proxy = proxyFactory.createProxyCollection(1L, TestOrder.class, TestOrderItem.class);

        assertAll(
                () -> assertThat(proxy).isNotNull(),
                () -> assertThat(proxy).hasSize(2)
        );
    }
}
