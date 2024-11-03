package persistence.sql.fixture;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class TestOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<TestOrderItem> orderItems;
}
