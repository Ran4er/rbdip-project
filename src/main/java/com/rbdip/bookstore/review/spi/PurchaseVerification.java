package com.rbdip.bookstore.review.spi;

/**
 * Port owned by the review module. Implementations may consult another module,
 * but review code itself stays independent from its internals.
 */
public interface PurchaseVerification {

    boolean hasRecordedPurchases();
}
