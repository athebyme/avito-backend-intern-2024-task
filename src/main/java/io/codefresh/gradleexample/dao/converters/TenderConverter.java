package io.codefresh.gradleexample.dao.converters;

import io.codefresh.gradleexample.dao.dto.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.Tender;

public class TenderConverter {
    public static TenderDTO toDTO(Tender tender){
        return new TenderDTO(
                tender.getId(),
                tender.getVersion(),
                tender.getDescription(),
                tender.getName(),
                tender.getCreatorUsername(),
                tender.getOrganization_id(),
                tender.getTender_status()
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
                dto.getTender_status()
        );
    }
}
