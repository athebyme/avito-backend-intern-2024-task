package io.codefresh.gradleexample.business.service.validators.values;

import io.codefresh.gradleexample.dao.entities.bids.Bid;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;

import java.util.List;
import java.util.UUID;

public interface ValidationServiceInterface {
    UUID checkUUID(String uuid);
    UUID checkUserExist(String username);
    UUID checkUserExist(UUID userID);
    Tender checkTenderExists(String tenderId);
    Bid checkBidExists(String bidId, String username);

    <T extends Enum<T>> boolean isValidEnumValue(String value,
                                                 Class<T> enumClass);
    <T extends Enum<T>> boolean isValidEnumValue(List<String> values,
                                                 Class<T> enumClass);
}
