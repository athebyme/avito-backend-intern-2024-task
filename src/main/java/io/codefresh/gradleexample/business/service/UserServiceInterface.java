package io.codefresh.gradleexample.business.service;

import io.codefresh.gradleexample.dao.dto.EmployeeDTO;

import java.util.List;
import java.util.UUID;

public interface UserServiceInterface {
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);
    EmployeeDTO updateEmployee(UUID id, EmployeeDTO employeeDTO);
    void deleteEmployee(UUID id);
    EmployeeDTO getEmployeeById(UUID id);
    List<EmployeeDTO> getAllEmployees();
}
