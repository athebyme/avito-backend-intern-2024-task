package io.codefresh.gradleexample.business.service.review.impl;

import io.codefresh.gradleexample.business.service.review.BidReviewServiceInterface;
import io.codefresh.gradleexample.business.service.validators.authorization.AuthorizationServiceInterface;
import io.codefresh.gradleexample.business.service.validators.values.ValidationServiceInterface;
import io.codefresh.gradleexample.dao.entities.bids.Bid;
import io.codefresh.gradleexample.dao.entities.bids.BidReview;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.repository.bids.BidRepository;
import io.codefresh.gradleexample.dao.repository.bids.BidReviewRepository;
import io.codefresh.gradleexample.exceptions.service.bids.BidNotFoundException;
import io.codefresh.gradleexample.exceptions.service.reviews.ReviewsNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BidReviewServiceImplementation implements BidReviewServiceInterface {
    private final BidRepository bidRepository;
    private final BidReviewRepository bidReviewRepository;
    private final ValidationServiceInterface validationService;
    private final AuthorizationServiceInterface authorizationService;

    @Autowired
    public BidReviewServiceImplementation(BidRepository bidRepository,
                                          BidReviewRepository bidReviewRepository,
                                          ValidationServiceInterface validationService,
                                          AuthorizationServiceInterface authorizationService){
        this.bidRepository = bidRepository;

        this.bidReviewRepository = bidReviewRepository;
        this.validationService = validationService;
        this.authorizationService = authorizationService;
    }

    public List<BidReview> getBidReviews(String tenderIdStr, String authorUsername, String requesterUsername, int limit, int offset) {
        UUID tenderId = validationService.checkUUID(tenderIdStr);
        UUID authorId = validationService.checkUserExist(authorUsername);
        UUID requesterId = validationService.checkUserExist(requesterUsername);

        Tender tender = validationService.checkTenderExists(String.valueOf(tenderId));
        authorizationService.checkUserOrganizationResponses(tender.getOrganization_id(), requesterId);

        List<Bid> bids = bidRepository.findByTenderIdAndAuthorId(tenderId, authorId);
        if (bids.isEmpty()) {
            throw new BidNotFoundException("Предложения автора для указанного тендера не найдены.");
        }

        List<BidReview> reviews = bidReviewRepository.findByBidIn(bids);

        if (reviews.isEmpty()) {
            throw new ReviewsNotFoundException("Отзывы на предложения не найдены.");
        }

        int fromIndex = Math.min(offset, reviews.size());
        int toIndex = Math.min(offset + limit, reviews.size());
        return reviews.subList(fromIndex, toIndex);
    }
}
