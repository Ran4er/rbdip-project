package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PricingCalculator {

    private static final int BULK_QUANTITY_THRESHOLD = 10;
    private static final BigDecimal BULK_FACTOR = new BigDecimal("0.95");
    private static final BigDecimal VIP_FACTOR = new BigDecimal("0.90");
    private static final BigDecimal WHOLESALE_FACTOR = new BigDecimal("0.85");
    private static final BigDecimal SAVE_TEN_AMOUNT = new BigDecimal("10");
    private static final BigDecimal SAVE_TWENTY_PERCENT_FACTOR = new BigDecimal("0.80");
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("1000");
    private static final BigDecimal HIGH_VALUE_FACTOR = new BigDecimal("0.98");
    private static final int MONEY_SCALE = 2;
    private static final String VIP = "vip";
    private static final String WHOLESALE = "wholesale";
    private static final String SAVE_TEN = "SAVE10";
    private static final String SAVE_TWENTY_PERCENT = "SAVE20PERCENT";

    public record LineItem(BigDecimal price, int quantity) {
    }

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = BigDecimal.ZERO;
        for (LineItem item : items) {
            total = total.add(calculateLinePrice(item));
        }
        total = applyCustomerDiscount(total, customerType);
        total = applyCoupon(total, couponCode);
        total = total.max(BigDecimal.ZERO);
        if (total.compareTo(HIGH_VALUE_THRESHOLD) > 0) {
            total = total.multiply(HIGH_VALUE_FACTOR);
        }
        return total.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLinePrice(LineItem item) {
        BigDecimal linePrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
        if (item.quantity() > BULK_QUANTITY_THRESHOLD) {
            return linePrice.multiply(BULK_FACTOR);
        }
        return linePrice;
    }

    private BigDecimal applyCustomerDiscount(BigDecimal total, String customerType) {
        if (VIP.equals(customerType)) {
            return total.multiply(VIP_FACTOR);
        }
        if (WHOLESALE.equals(customerType)) {
            return total.multiply(WHOLESALE_FACTOR);
        }
        return total;
    }

    private BigDecimal applyCoupon(BigDecimal total, String couponCode) {
        if (SAVE_TEN.equals(couponCode)) {
            return total.subtract(SAVE_TEN_AMOUNT);
        }
        if (SAVE_TWENTY_PERCENT.equals(couponCode)) {
            return total.multiply(SAVE_TWENTY_PERCENT_FACTOR);
        }
        return total;
    }
}
