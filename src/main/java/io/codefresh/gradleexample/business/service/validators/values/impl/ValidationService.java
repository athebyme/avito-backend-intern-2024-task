package io.codefresh.gradleexample.business.service.validators.values.impl;

import io.codefresh.gradleexample.business.service.validators.values.ValidationServiceInterface;
import io.codefresh.gradleexample.dao.entities.bids.Bid;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import io.codefresh.gradleexample.dao.entities.users.Employee;
import io.codefresh.gradleexample.dao.repository.bids.BidRepository;
import io.codefresh.gradleexample.dao.repository.tenders.TenderRepository;
import io.codefresh.gradleexample.dao.repository.users.UserRepository;
import io.codefresh.gradleexample.exceptions.service.InvalidUUIDException;
import io.codefresh.gradleexample.exceptions.service.bids.BidNotFoundException;
import io.codefresh.gradleexample.exceptions.service.employee.EmployeeNotFoundException;
import io.codefresh.gradleexample.exceptions.service.tenders.TenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Вообще, сервис сделан для удобства.
 * Мне кажется SRP не сильно нарушается здесь.
 * Уменьшает повторение кода.
 */
@Component
public class ValidationService implements ValidationServiceInterface {
    private final UserRepository userRepository;
    private final TenderRepository tenderRepository;
    private final BidRepository bidRepository;

    @Autowired
    public ValidationService(UserRepository userRepository, TenderRepository tenderRepository, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.tenderRepository = tenderRepository;
        this.bidRepository = bidRepository;
    }

    @Override
    public UUID checkUUID(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Неверный формат запроса или его параметры.");
        }
    }

    @Override
    public UUID checkUserExist(String username) {
        UUID userId = userRepository.findUserByUsername(username).getId();
        if (userId == null) {
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }
        return userId;
    }

    @Override
    public UUID checkUserExist(UUID userID){
        Optional<Employee> employee = userRepository.findById(userID);
        if (!employee.isPresent()) {
            throw new EmployeeNotFoundException("Пользователь не существует или некорректен.");
        }
        return userID;
    }

    @Override
    public Tender checkTenderExists(String tenderId) {
        checkUUID(tenderId);
        return tenderRepository.findById(UUID.fromString(tenderId))
                .orElseThrow(() -> new TenderNotFoundException("Тендер не найден."));
    }

    @Override
    public Bid checkBidExists(String bidId, String username) {
        checkUUID(bidId);
        checkUserExist(username);
        Optional<Bid> bid = bidRepository.findById(UUID.fromString(bidId));
        if (!bid.isPresent()) {
            throw new BidNotFoundException("Предложение не найдено.");
        }
        return bid.get();
    }

    @Override
    public <T extends Enum<T>> boolean isValidEnumValue(List<String> values,
                                                         Class<T> enumClass) {
        for (String value : values) {
            boolean found = false;
            for (T enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equals(value)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <T extends Enum<T>> boolean isValidEnumValue(String value,
                                                         Class<T> enumClass) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
