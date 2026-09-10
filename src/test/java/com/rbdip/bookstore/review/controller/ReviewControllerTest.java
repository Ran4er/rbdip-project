package com.rbdip.bookstore.review.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rbdip.bookstore.review.model.Review;
import com.rbdip.bookstore.review.service.ReviewService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ReviewControllerTest {

    private final ReviewService service = mock(ReviewService.class);
    private final ReviewController controller = new ReviewController(service);

    @Test
    void addsReviewAndReturnsItsId() {
        Review review = mock(Review.class);
        when(review.getId()).thenReturn(17L);
        when(service.addReview(3L, "Anna", 5, "Great")).thenReturn(review);

        Map<String, Object> result = controller.addReview(3L, Map.of(
                "authorName", "Anna", "rating", 5, "comment", "Great"));

        assertThat(result).containsEntry("id", 17L);
        verify(service).addReview(3L, "Anna", 5, "Great");
    }

    @Test
    void listsReviewsAndNormalizesNullComments() {
        Review review = new Review(3L, "Ivan", 4, null);
        when(service.listReviews(3L)).thenReturn(List.of(review));

        assertThat(controller.listReviews(3L)).containsExactly(Map.of(
                "authorName", "Ivan", "rating", 4, "comment", ""));
    }
}
