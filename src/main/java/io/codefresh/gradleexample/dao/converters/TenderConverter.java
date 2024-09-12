package io.codefresh.gradleexample.dao.converters;

import io.codefresh.gradleexample.dao.dto.tenders.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;
import org.springframework.stereotype.Component;

@Component
public class TenderConverter {
    public static TenderDTO toDTO(Tender tender){
        return new TenderDTO(
                tender.getId(),
                tender.getVersion(),
                tender.getDescription(),
                tender.getName(),
                tender.getCreatorUsername(),
                tender.getOrganization_id(),
                tender.getStatus(),
                tender.getCreated_at()
        );
    }

    public static Tender toEntity(TenderDTO dto){
        return new Tender(
                dto.getId(),
                dto.getVersion(),
                dto.getDescription(),
                dto.getName(),
                dto.getCreatorUsername(),
                dto.getOrganization_id(),
                dto.getTender_status(),
                null,
                dto.getCreated_at()
        );
    }
}
