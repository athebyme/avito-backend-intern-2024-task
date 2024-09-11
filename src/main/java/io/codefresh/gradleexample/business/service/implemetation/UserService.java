package io.codefresh.gradleexample.business.service.implemetation;

import io.codefresh.gradleexample.business.service.UserServiceInterface;
import io.codefresh.gradleexample.dao.converters.EmployeeConverter;
import io.codefresh.gradleexample.dao.dto.EmployeeDTO;
import io.codefresh.gradleexample.dao.entities.users.EmployeeEntity;
import io.codefresh.gradleexample.dao.repository.UserRepository;
import io.codefresh.gradleexample.exceptions.service.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        EmployeeEntity employeeEntity = EmployeeConverter.toEntity(employeeDTO);
        employeeEntity.setCreated_at(new Timestamp(System.currentTimeMillis()));
        employeeEntity.setUpdated_at(employeeEntity.getCreated_at());

        employeeEntity = userRepository.save(employeeEntity);

        return EmployeeConverter.toDTO(employeeEntity);
    }


    @Override
    public EmployeeDTO updateEmployee(UUID id, EmployeeDTO employeeDTO) {
        EmployeeEntity existingEmployee = userRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        existingEmployee.setUsername(employeeDTO.getUsername());
        existingEmployee.setFirst_name(employeeDTO.getFirstName());
        existingEmployee.setLast_name(employeeDTO.getLastName());
        existingEmployee.setUpdated_at(new Timestamp(System.currentTimeMillis()));

        existingEmployee = userRepository.save(existingEmployee);

        return EmployeeConverter.toDTO(existingEmployee);
    }

    @Override
    public void deleteEmployee(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public EmployeeDTO getEmployeeById(UUID id) {
        EmployeeEntity employeeEntity = userRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return EmployeeConverter.toDTO(employeeEntity);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return userRepository.findAll()
                .stream()
                .map(EmployeeConverter::toDTO)
                .collect(Collectors.toList());
    }
}
