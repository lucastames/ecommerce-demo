package org.tames.ecommercecrud.modules.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tames.ecommercecrud.modules.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {}
