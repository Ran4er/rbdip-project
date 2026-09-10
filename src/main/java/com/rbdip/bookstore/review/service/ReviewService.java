package com.rbdip.bookstore.review.service;

import com.rbdip.bookstore.review.model.Review;
import com.rbdip.bookstore.review.repository.ReviewRepository;
import com.rbdip.bookstore.review.spi.PurchaseVerification;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private static final String ANONYMOUS = "anonymous";

    private final ReviewRepository reviewRepository;
    private final PurchaseVerification purchaseVerification;

    public ReviewService(ReviewRepository reviewRepository, PurchaseVerification purchaseVerification) {
        this.reviewRepository = reviewRepository;
        this.purchaseVerification = purchaseVerification;
    }

    public Review addReview(Long productId, String authorName, Integer rating, String comment) {
        purchaseVerification.hasRecordedPurchases();
        String resolvedAuthor = authorName == null ? ANONYMOUS : authorName;
        return reviewRepository.save(new Review(productId, resolvedAuthor, rating, comment));
    }

    public List<Review> listReviews(Long productId) {
        return reviewRepository.findByProductId(productId);
    }
}
