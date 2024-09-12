package io.codefresh.gradleexample.dao.converters;


import io.codefresh.gradleexample.dao.dto.EmployeeDTO;
import io.codefresh.gradleexample.dao.entities.users.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeConverter {
    public static EmployeeDTO toDTO(Employee entity) {
        return new EmployeeDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getFirst_name(),
                entity.getLast_name()
        );
    }

    public static Employee toEntity(EmployeeDTO dto) {
        Employee entity = new Employee();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setFirst_name(dto.getFirstName());
        entity.setLast_name(dto.getLastName());
        return entity;
    }
}
