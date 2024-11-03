package persistence.sql.fixture;

import jakarta.persistence.*;

import java.util.List;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<TestOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TestOrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
