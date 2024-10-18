package com.koreait.exam.springbatch_10.app.order.entity;

import com.koreait.exam.springbatch_10.app.base.entity.BaseEntity;
import com.koreait.exam.springbatch_10.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Table(name="product_order")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);

        orderItems.add(orderItem);
    }
}

// 관계가 존재하면 필수
// @ManyToOne : 다대일 관계
// 다수의 엔티티가 하나의 엔티티에 연결됨
// OrderItem(주문 항목) 은 Order(주문)dp thrgksek.
//OrderItem이 -> Order를 참조해야해. 그래서 @ManyToOne 이다.

// 필수는 아니지만, 관계를 맺고있는데 한쪽은 @ManyToOne 인데, @OneToMany가 안써있을 때, 파악하기 어렵다.
// 관계를 명확히 할 때 써줌
// @OneToMany : 일대다 관계
// 하나의 엔티티가 여러 하위 엔티티를 가질 때 필수
// 하나의 Order(주문)이 여러 OrderItem(주문 항목)을 가진다.