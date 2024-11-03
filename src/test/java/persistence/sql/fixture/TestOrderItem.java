package persistence.sql.fixture;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class TestOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    private Integer quantity;
}
