package com.koreait.exam.springbatch_10.app.order.service;

import com.koreait.exam.springbatch_10.app.cart.entity.CartItem;
import com.koreait.exam.springbatch_10.app.cart.service.CartService;
import com.koreait.exam.springbatch_10.app.member.entity.Member;
import com.koreait.exam.springbatch_10.app.member.service.MemberService;
import com.koreait.exam.springbatch_10.app.order.entity.Order;
import com.koreait.exam.springbatch_10.app.order.entity.OrderItem;
import com.koreait.exam.springbatch_10.app.order.repository.OrderRepository;
import com.koreait.exam.springbatch_10.app.product.entity.ProductOption;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final MemberService memberService;

    // 전달 받은 회원의 장바구니에 있는 아이템들을 전부 가져와서 주문으로 변환하는 과정
    public Order createFromCart(Member member) {

        // 만약에 장바구니의 특정 상품이 판매 불가 상태야 => 삭제
        // 만약에 장바구니의 특정 상품이 판매 가능 상태야 => 주문 품목으로 옮긴 후 삭제
        List<CartItem> cartItems = cartService.getItemsByMember(member);    // 회원이 장바구니에 담아 둔 모든 상품을 가져오는 것

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {   //cartItem 에 있는것을 가져와야함.
            ProductOption productOption = cartItem.getProductOption();  //어떤 옵션인지 가져오고

            if (productOption.isOrderable(cartItem.getQuantity())) {    //주문한것인지 아닌지 가져와야함
                orderItems.add(new OrderItem(productOption, cartItem.getQuantity()));   // 새로 올림
            }

            cartService.deleteItem(cartItem);   // 장바구니에서 삭제되고
        }

        return create(member, orderItems);  // 주문은 따로 생성
    }

    @Transactional
    public Order create(Member member, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .member(member)
                .build();       // 빌더패턴을 통한 객체 생성 = new와 같다, 데이터베이스에 저장하기위한 목적

        // orderItem을 순회하면서 orderItems에 추가
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);    // 그리고 저장

        return order;
    }

    @Transactional
    public void payByRestCashOnly(Order order) {
        Member orderer = order.getMember();

        long restCash = orderer.getRestCash();

        int payPrice = order.calculatePayPrice();

        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족해");
        }

        memberService.addCash(orderer, payPrice * -1, "주문결제_예치금결제");

        order.setPaymentDone();

        orderRepository.save(order);
    }
}

// @Transactional(readOnly = true) : 스프링에서 트랜잭션 관리에 사용되는 어노테이션
// 트랜잭션은 데이터베이스에서 여러 작업을 하나의 단위로 묶어 처리하는 기능을 의미하며, 이 과정에서 모든 작업이 성공적으로 완료되거나, 하나라도 실패하면 모든 작업이 취소(rollback)되어야 함
// @Transactional
// 데이터의 일관성을 유지하는 것 중 하나