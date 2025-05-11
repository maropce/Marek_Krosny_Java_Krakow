package pl.maropce.payment;

import org.json.JSONArray;
import org.json.JSONObject;
import pl.maropce.payment.strategy.*;
import pl.maropce.wallet.ClientWallet;
import pl.maropce.order.Order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PaymentMethodService {

    private final PaymentMethodMapper paymentMethodMapper;

    public PaymentMethodService(PaymentMethodMapper paymentMethodMapper) {
        this.paymentMethodMapper = paymentMethodMapper;
    }

    public List<Order> sortByNumberOfPromotionsDesc(List<Order> orders) {
        return orders.stream()
                .sorted(Comparator.comparingInt(order -> order.getPromotions().size()))
                .toList()
                .reversed();
    }

    public List<PaymentMethod> importPaymentMethods(String path) throws IOException {

        JSONArray json = new JSONArray(Files.readString(Path.of(path)));

        List<PaymentMethod> paymentMethods = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            JSONObject obj = json.getJSONObject(i);
            PaymentMethod paymentMethod = paymentMethodMapper.toPaymentMethod(obj);
            paymentMethods.add(paymentMethod);
        }

        return paymentMethods;
    }

    public PaymentStrategy findBestStrategyForPayment(ClientWallet wallet, Order order) {

        List<PaymentMethod> allPaymentMethods = wallet.getPaymentMethods();

        List<PaymentMethod> availablePaymentMethods = findAvailableCardPromotionMethods(order, allPaymentMethods);
        availablePaymentMethods = new ArrayList<>(sortMethodPaymentsByBestDiscount(availablePaymentMethods));

        PaymentMethod pointsMethod = wallet.getPointsMethod();

        if (availablePaymentMethods.isEmpty()) {
            availablePaymentMethods.add(pointsMethod);
        }
        for (PaymentMethod paymentMethod : availablePaymentMethods) {

            if (pointsMethod.getDiscount() >= paymentMethod.getDiscount()) {
                if (canBePaidByPointsOnly(pointsMethod, order)) {
                    return new OneMethodPayment(pointsMethod);
                }
            }

            if (canBePaidByCardOnly(paymentMethod, order)) {
                return new OneMethodPayment(paymentMethod);
            }

            if (hasEnoughPointsForSplitDiscount(pointsMethod, order)) {
                PaymentMethod bestForSplitPay = findBestCardForSplitPay(allPaymentMethods, order);
                if (bestForSplitPay != null) {
                    return new PointAndCardPayment(pointsMethod, bestForSplitPay);
                }

                PaymentStrategy paymentStrategy = findSplitPaymentWithIncreasedPointsIfPossible(pointsMethod, allPaymentMethods, order);
                if (paymentStrategy != null) {
                    return paymentStrategy;
                }
            }
        }
        PaymentMethod bestCardWithoutPromotion = findAnyCardForPay(allPaymentMethods, order);

        if (bestCardWithoutPromotion != null) {
            return new NoPromotionPayment(bestCardWithoutPromotion);
        }
        return null;
    }

    private boolean canBePaidByPointsOnly(PaymentMethod pointsMethod, Order order) {
        double value = order.calculateValueAfterDiscount(pointsMethod.getDiscount());
        return pointsMethod.canPayFor(value);
    }

    private boolean canBePaidByCardOnly(PaymentMethod cardMethod, Order order) {
        double value = order.calculateValueAfterDiscount(cardMethod.getDiscount());
        return cardMethod.canPayFor(value);
    }

    private boolean hasEnoughPointsForSplitDiscount(PaymentMethod pointsMethod, Order order) {
        return pointsMethod.canPayFor(order.getValue() * 0.1);
    }

    private PaymentStrategy findSplitPaymentWithIncreasedPointsIfPossible(PaymentMethod pointsMethod, List<PaymentMethod> allPaymentMethods, Order order) {
        double maxPointsAmount = pointsMethod.getRemainingLimit();

        allPaymentMethods = allPaymentMethods.stream()
                .filter(method -> {
                    if (method.isPointsMethod()) return false;
                    return method.canPayFor(order.getValue() * 0.9 - maxPointsAmount);
                })
                .sorted(Comparator.comparingDouble(PaymentMethod::getRemainingLimit))
                .toList();

        for (PaymentMethod paymentMethod : allPaymentMethods) {
            double remainingAmountToPayByPoints = (order.getValue() * 0.9) - paymentMethod.getRemainingLimit();
            if (remainingAmountToPayByPoints >= order.getValue() * 0.1) {
                return new IncreasedPointAndCardPayment(pointsMethod, paymentMethod, remainingAmountToPayByPoints);
            }
        }
        return null;
    }

    private PaymentMethod findBestCardForSplitPay(List<PaymentMethod> paymentMethods, Order order) {
        double payByPoints = order.getValue() * 0.1;
        double payByCard = (order.getValue() * 0.9) - payByPoints;

        paymentMethods = sortByRemainingLimitAsc(paymentMethods);

        for (PaymentMethod cardMethod : paymentMethods) {
            if (cardMethod.isPointsMethod()) {
                continue;
            }
            if (cardMethod.canPayFor(payByCard)) {
                return cardMethod;
            }
        }
        return null;
    }

    private PaymentMethod findAnyCardForPay(List<PaymentMethod> allPaymentMethods, Order order) {

        allPaymentMethods = sortByRemainingLimitAsc(allPaymentMethods);

        return allPaymentMethods.stream()
                .filter(paymentMethod ->
                        paymentMethod.canPayFor(order.getValue()))
                .findFirst()
                .orElse(null);
    }
    private List<PaymentMethod> sortByRemainingLimitAsc(List<PaymentMethod> paymentMethods) {
        return paymentMethods.stream()
                .sorted(Comparator.comparingDouble(PaymentMethod::getRemainingLimit))
                .toList();
    }

    public PaymentMethod findCardToPayInsteadOfPoints(List<PaymentMethod> paymentMethods, Order order) {
        return paymentMethods.stream()
                .filter(method -> {
                    if (method.isPointsMethod()) {
                        return false;
                    }
                    return method.canPayFor(order.calculateValueAfterDiscount(method.getDiscount()));
                })
                .min(Comparator.comparingDouble(PaymentMethod::getRemainingLimit))
                .orElse(null);

    }

    private List<PaymentMethod> sortMethodPaymentsByBestDiscount(List<PaymentMethod> paymentMethods) {
        return paymentMethods.stream()
                .sorted(this::compareByBestDiscount)
                .toList();
    }

    private int compareByBestDiscount(PaymentMethod method1, PaymentMethod method2) {
        if (method1.getDiscount() == method2.getDiscount()) {
            if (method1.isPointsMethod()) {
                return -1;
            } else if (method2.isPointsMethod()) {
                return 1;
            } else {
                return 0;
            }
        }
        return method1.getDiscount() < method2.getDiscount() ? 1 : -1;
    }

    private List<PaymentMethod> findAvailableCardPromotionMethods(Order order, List<PaymentMethod> allPaymentMethods) {

        List<PaymentMethod> availablePromotions = new ArrayList<>();

        if (order.getPromotions() != null && !order.getPromotions().isEmpty()) {
            List<PaymentMethod> list = allPaymentMethods.stream()
                    .filter(method ->
                            order.getPromotions().contains(method.getId()))
                    .toList();

            availablePromotions.addAll(list);
        }

        return availablePromotions;
    }
}
