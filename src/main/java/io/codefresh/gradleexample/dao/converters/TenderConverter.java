package io.codefresh.gradleexample.dao.converters;

import io.codefresh.gradleexample.dao.dto.TenderDTO;
import io.codefresh.gradleexample.dao.entities.tenders.TenderEntity;

public class TenderConverter {
    public static TenderDTO toDTO(TenderEntity tenderEntity){
        return new TenderDTO(
                tenderEntity.getId(),
                tenderEntity.getVersion(),
                tenderEntity.getDescription(),
                tenderEntity.getName(),
                tenderEntity.getCreatorUsername(),
                tenderEntity.getOrganization_id(),
                tenderEntity.getTender_status()
        );
    }

    public static TenderEntity toEntity(TenderDTO dto){
        return new TenderEntity(
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
