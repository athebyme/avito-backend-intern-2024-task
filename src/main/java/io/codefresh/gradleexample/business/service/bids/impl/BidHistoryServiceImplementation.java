package io.codefresh.gradleexample.business.service.bids.impl;

import io.codefresh.gradleexample.business.service.bids.BidHistoryServiceInterface;
import io.codefresh.gradleexample.dao.converters.bids.BidConverter;
import io.codefresh.gradleexample.dao.dto.bids.BidDTO;
import io.codefresh.gradleexample.dao.entities.bids.Bid;
import io.codefresh.gradleexample.dao.entities.bids.BidHistory;
import io.codefresh.gradleexample.dao.repository.bids.BidHistoryRepository;
import io.codefresh.gradleexample.dao.repository.bids.BidRepository;
import io.codefresh.gradleexample.exceptions.service.bids.BidNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class BidHistoryServiceImplementation implements BidHistoryServiceInterface {

    private final BidHistoryRepository bidHistoryRepository;
    private final BidRepository bidsRepository;

    @Autowired
    public BidHistoryServiceImplementation(BidHistoryRepository bidHistoryRepository, BidRepository bidsRepository) {
        this.bidHistoryRepository = bidHistoryRepository;
        this.bidsRepository = bidsRepository;
    }


    @Override
    public void saveBidHistory(Bid bid) {
        BidHistory bidHistory = new BidHistory(bid);
        bidHistoryRepository.save(bidHistory);
    }

    @Override
    @Transactional
    public BidDTO rollbackBid(UUID bidId, int version, String username) {
        Bid bid = bidsRepository.findById(bidId)
                .orElseThrow(() -> new BidNotFoundException("Предложение с ID " + bidId + " не найдено."));
        BidHistory targetVersion = bidHistoryRepository.findByBidIdAndVersion(bidId, version)
                .orElseThrow(() -> new BidNotFoundException("Версия " + version + " для предложения " + bidId + " не найдена."));

        // Проверка прав пользователя (можно вызывать из основного сервиса, если нужно)

        Bid clonedBid = bid.clone();
        clonedBid.setName(targetVersion.getName());
        clonedBid.setDescription(targetVersion.getDescription());
        clonedBid.setAuthorType(targetVersion.getAuthorType());
        clonedBid.setAuthorId(targetVersion.getAuthorId());
        clonedBid.setTenderId(targetVersion.getTenderId());
        clonedBid.setVersion(bid.getVersion() + 1);
        clonedBid.setReview(targetVersion.getBid().getReview());
        clonedBid.setReviewDescription(targetVersion.getReviewDescription());
        clonedBid.setCreated_at(targetVersion.getCreated_at());
        clonedBid.setUpdated_at(new Timestamp(System.currentTimeMillis()));

        saveBidHistory(clonedBid);

        return BidConverter.toDTO(clonedBid);
    }
}
