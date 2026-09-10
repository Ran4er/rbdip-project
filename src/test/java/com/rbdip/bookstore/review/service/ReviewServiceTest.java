package com.rbdip.bookstore.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.review.model.Review;
import com.rbdip.bookstore.review.repository.ReviewRepository;
import com.rbdip.bookstore.review.spi.PurchaseVerification;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReviewServiceTest {

    private final ReviewRepository repository = mock(ReviewRepository.class);
    private final PurchaseVerification purchaseVerification = mock(PurchaseVerification.class);
    private final ReviewService service = new ReviewService(repository, purchaseVerification);

    @Test
    void storesAnonymousReviewWithoutDependingOnOrderInternals() {
        when(repository.save(org.mockito.ArgumentMatchers.any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Review review = service.addReview(7L, null, 5, "Great");

        assertThat(review.getProductId()).isEqualTo(7L);
        assertThat(review.getAuthorName()).isEqualTo("anonymous");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getComment()).isEqualTo("Great");
        verify(purchaseVerification).hasRecordedPurchases();
        verify(repository).save(review);
    }

    @Test
    void returnsReviewsFromModuleRepository() {
        Review review = new Review(3L, "Ivan", 4, "Good");
        when(repository.findByProductId(3L)).thenReturn(List.of(review));

        assertThat(service.listReviews(3L)).containsExactly(review);
        verify(repository).findByProductId(3L);
    }
}
