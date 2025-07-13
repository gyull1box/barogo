package com.example.barogo.service;

import com.example.barogo.domain.*;
import com.example.barogo.dto.OrderDto;
import com.example.barogo.dto.OrderModifyRequest;
import com.example.barogo.dto.UserRegisterRequest;
import com.example.barogo.exception.PasswordExpiredException;
import com.example.barogo.exception.UnauthorizedException;
import com.example.barogo.repository.AddressRepository;
import com.example.barogo.repository.OrderRepository;
import com.example.barogo.repository.UserRepository;
import com.example.barogo.type.OrderStatusType;
import com.example.barogo.type.UserType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final OrderRepository orderRepository;

    private final AddressRepository addressRepository;

    private final static int FAIL_PERMIT_COUNT = 5;
    private final JPAQueryFactory jpaQueryFactory;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       OrderRepository orderRepository,
                       JPAQueryFactory jpaQueryFactory,
                       AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.orderRepository = orderRepository;
        this.jpaQueryFactory = jpaQueryFactory;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public void register(UserRegisterRequest userRegisterRequest){
        // 연락처를 비롯한 개인정보 인증 후 이미 가입된 회원인지 확인필요
        String id = userRegisterRequest.getUserId().toLowerCase().trim();
        if(userRepository.existsByUserId(id)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setName(userRegisterRequest.getName());
        user.setCreateUser(id);
        user.setUserId(id);

        LocalDateTime now = LocalDateTime.now();

        user.setCreateDate(now);
        user.setPhone(userRegisterRequest.getPhone());
        user.setPasswordExpireDate(now.plusDays(90).toLocalDate());
        user.setStartUseDttm(now);
        user.setType(UserType.CUSTOMER.getCode()); // 회원가입을 통해 등록된 사용자는 일반 소비자로 분류한다는 가정

        userRepository.save(user);
    }

    @Transactional
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
    }

    @Transactional
    public void increaseFailedCount(User user) {
        int newCnt = user.getFailedCount() + 1;
        user.setFailedCount(newCnt);

        if (newCnt >= FAIL_PERMIT_COUNT) {
            user.setValidYn('N'); // 본인인증 다시 -> 재설정 필요
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedCount(User user){
        user.setFailedCount(0);
        userRepository.save(user);
    }

    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

    @Transactional
    public User authenticate(String userId, String rawPwd) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 계정"));

        if (user.isAccountLocked()) {
            throw new UnauthorizedException("계정이 잠겨 있습니다.");
        }

        if (user.isPasswordExpired()) {
            throw new PasswordExpiredException("비밀번호가 만료되었습니다.");
        }

        if (!passwordEncoder.matches(rawPwd, user.getPassword())) {
            increaseFailedCount(user);
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        if (user.getFailedCount() > 0) {
            resetFailedCount(user);
        }

        return user;
    }


    public Page<OrderDto> searchOrders(
            String userId,
            Date fromDate, Date toDate,
            @Nullable OrderStatusType status,
            int page, int limit) {

        int pg = page <= 0 ? 1 : page;
        int lm = limit <= 0 ? 100 : limit;

        Pageable pageable = PageRequest.of(pg - 1, lm, Sort.by(Sort.Direction.DESC, "deliveryDate"));

        QOrderEntity orderEntity = QOrderEntity.orderEntity;
        QPayment payment = QPayment.payment;
        QAddress frmAddr = QAddress.address;
        QAddress toAddr = new QAddress("toAddr");

        BooleanBuilder cond = new BooleanBuilder();

        if (userId != null) {
            cond.and(orderEntity.userId.userId.eq(userId));
        } else {
            cond.and(orderEntity.userId.userId.isNull());
        }

        if (fromDate != null && toDate != null) {
            cond.and(orderEntity.deliveryDate.between(fromDate, toDate));
        }


        if (status != null && status.name() != null) {
            cond.and(orderEntity.orderStatus.eq(status.name()));
        }

        JPAQuery<OrderEntity> query = jpaQueryFactory
                .select(orderEntity)
                .from(orderEntity)
                .join(orderEntity.paymentId, payment).fetchJoin()
                .leftJoin(orderEntity.frmAddress, frmAddr).fetchJoin()
                .leftJoin(orderEntity.toAddress, toAddr).fetchJoin()
                .where(cond)
                .orderBy(orderEntity.deliveryDate.desc());

        long total = query.fetchCount();

        List<OrderEntity> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(
                content.stream().map(OrderDto::fromEntity).toList(),
                pageable,
                total);
    }

    @Transactional
    public OrderDto modifyOrder(String userId, Long orderId, OrderModifyRequest request) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getUserId().getUserId().equals(userId)) {
            throw new AccessDeniedException("본인의 주문만 수정할 수 있습니다.");
        }

        if (OrderStatusType.CANCELLED.getStatus().equals(order.getOrderStatus())) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        if (order.getOrderStatus().compareTo(OrderStatusType.CONFIRM.getStatus()) >= 0) {
            throw new IllegalStateException("확정된 주문은 수정할 수 없습니다.");
        }

        Address toAddress = order.getToAddress();
        if (request.getZipCode() != null) toAddress.setZipCode(request.getZipCode());
        if (request.getCity() != null) toAddress.setCity(request.getCity());
        if (request.getDistrict() != null) toAddress.setDistrict(request.getDistrict());
        if (request.getDetail() != null) toAddress.setDetail(request.getDetail());
        if (request.getPhone() != null) toAddress.setPhone(request.getPhone());
        if (request.getRecipientName() != null) toAddress.setRecipientName(request.getRecipientName());
        if (request.getAddressName() != null) toAddress.setAddressName(request.getAddressName());

        toAddress.setUpdateDate(LocalDateTime.now());
        toAddress.setUpdateUser(userId);

        order.setMemo(request.getMemo());

        addressRepository.save(toAddress);
        order.setUpdateDate(LocalDateTime.now());
        order.setUpdateUser(userId);
        OrderEntity savedOrder = orderRepository.save(order);

        return OrderDto.fromEntity(savedOrder);
    }

}
