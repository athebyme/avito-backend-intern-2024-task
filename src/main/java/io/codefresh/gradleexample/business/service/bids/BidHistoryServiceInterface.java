package io.codefresh.gradleexample.business.service.bids;

import io.codefresh.gradleexample.dao.dto.bids.BidDTO;
import io.codefresh.gradleexample.dao.entities.bids.Bid;

import java.util.UUID;

public interface BidHistoryServiceInterface {
    void saveBidHistory(Bid tender);
    BidDTO rollbackBid(UUID bidId, int version, String username);

}
