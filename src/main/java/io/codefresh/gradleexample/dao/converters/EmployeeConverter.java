package io.codefresh.gradleexample.dao.converters;

import io.codefresh.gradleexample.dao.dto.EmployeeDTO;
import io.codefresh.gradleexample.dao.entities.users.EmployeeEntity;

public class EmployeeConverter {
    public static EmployeeDTO toDTO(EmployeeEntity entity) {
        return new EmployeeDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getFirst_name(),
                entity.getLast_name()
        );
    }

    public static EmployeeEntity toEntity(EmployeeDTO dto) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setFirst_name(dto.getFirstName());
        entity.setLast_name(dto.getLastName());
        return entity;
    }
}
