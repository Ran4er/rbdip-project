package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void appliesBulkDiscountOnlyAboveTenItems() {
        assertTotal("100.00", List.of(item("10.00", 10)), "regular", null);
        assertTotal("104.50", List.of(item("10.00", 11)), "regular", null);
    }

    @Test
    void appliesVipDiscount() {
        assertTotal("90.00", List.of(item("100.00", 1)), "vip", null);
    }

    @Test
    void appliesWholesaleDiscount() {
        assertTotal("85.00", List.of(item("100.00", 1)), "wholesale", null);
    }

    @Test
    void leavesUnknownCustomerTypeUndiscounted() {
        assertTotal("100.00", List.of(item("100.00", 1)), "partner", null);
    }

    @Test
    void subtractsSaveTenCoupon() {
        assertTotal("90.00", List.of(item("100.00", 1)), "regular", "SAVE10");
    }

    @Test
    void appliesTwentyPercentCoupon() {
        assertTotal("80.00", List.of(item("100.00", 1)), "regular", "SAVE20PERCENT");
    }

    @Test
    void ignoresUnknownCoupon() {
        assertTotal("100.00", List.of(item("100.00", 1)), "regular", "OTHER");
    }

    @Test
    void clampsNegativeTotalToZero() {
        assertTotal("0.00", List.of(item("5.00", 1)), "regular", "SAVE10");
    }

    @Test
    void appliesHighValueDiscountOnlyAboveThreshold() {
        assertTotal("1000.00", List.of(item("1000.00", 1)), "regular", null);
        assertTotal("980.98", List.of(item("1001.00", 1)), "regular", null);
    }

    @Test
    void appliesDiscountsInOriginalOrder() {
        assertTotal("1058.40", List.of(item("1500.00", 1)), "vip", "SAVE20PERCENT");
    }

    @Test
    void sumsMultipleLinesBeforeCustomerDiscounts() {
        assertTotal(
                "27.00",
                List.of(item("10.00", 1), item("20.00", 1)),
                "vip",
                null);
    }

    @Test
    void roundsMoneyHalfUpToTwoDecimals() {
        assertTotal("0.34", List.of(item("0.335", 1)), "regular", null);
    }

    @Test
    void returnsZeroForEmptyOrder() {
        assertTotal("0.00", List.of(), "regular", null);
    }

    private void assertTotal(
            String expected,
            List<PricingCalculator.LineItem> items,
            String customerType,
            String couponCode) {
        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);
        assertThat(total).isEqualByComparingTo(expected);
    }

    private PricingCalculator.LineItem item(String price, int quantity) {
        return new PricingCalculator.LineItem(new BigDecimal(price), quantity);
    }
}
