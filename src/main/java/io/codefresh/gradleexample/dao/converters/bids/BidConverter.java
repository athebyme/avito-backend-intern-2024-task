package io.codefresh.gradleexample.dao.converters.bids;

import io.codefresh.gradleexample.dao.dto.bids.BidDTO;
import io.codefresh.gradleexample.dao.entities.bids.Bid;
import org.springframework.stereotype.Component;

@Component
public class BidConverter {
    public static BidDTO toDTO(Bid entity) {
        return new BidDTO(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                entity.getAuthorType(),
                entity.getAuthorId(),
                entity.getVersion(),
                entity.getCreated_at()
        );
    }

    public static Bid toEntity(BidDTO dto) {
        Bid entity = new Bid();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        entity.setAuthorType(dto.getAuthorType());
        entity.setVersion(dto.getVersion());
        entity.setCreated_at(dto.getCreated_at());
        entity.setAuthorType(dto.getAuthorType());
        return entity;
    }
}
