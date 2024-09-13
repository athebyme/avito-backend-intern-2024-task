package io.codefresh.gradleexample.business.service.bids;

import io.codefresh.gradleexample.dao.dto.bids.BidDTO;
import io.codefresh.gradleexample.dao.entities.bids.BidsStatuses;

import java.util.List;
import java.util.Map;

public interface BidServiceInterface {
    BidDTO createBid(String name, String description, String tenderId, String authorType, String authorId);
    List<BidDTO> getBidsByUsername(Integer limit, Integer offset, String username);
    List<BidDTO> getTenderBids(Integer limit, Integer offset, String username, String tenderId);
    BidsStatuses getBidsStatuses(String bidId, String username);
    BidDTO updateBidStatus(String bidId, String status, String username);
    BidDTO updateBid(String bidId, String username, Map<String, Object> updates);
    BidDTO rollbackBid(String bidId, String username, int version);
    void submitDecision(String bidId, String decision, String username);
    void submitFeedback(String bidId, String feedback, String username);
}
