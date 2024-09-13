package io.codefresh.gradleexample.business.service.implementation;

import io.codefresh.gradleexample.business.service.UserServiceInterface;
import io.codefresh.gradleexample.dao.converters.EmployeeConverter;
import io.codefresh.gradleexample.dao.dto.EmployeeDTO;
import io.codefresh.gradleexample.dao.entities.users.Employee;
import io.codefresh.gradleexample.dao.repository.UserRepository;
import io.codefresh.gradleexample.exceptions.service.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserServiceInterface {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = EmployeeConverter.toEntity(employeeDTO);
        employee.setCreated_at(new Timestamp(System.currentTimeMillis()));
        employee.setUpdated_at(employee.getCreated_at());

        employee = userRepository.save(employee);

        return EmployeeConverter.toDTO(employee);
    }


    @Override
    public EmployeeDTO updateEmployee(UUID id, EmployeeDTO employeeDTO) {
        Employee existingEmployee = userRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Пользователь не существует или некорректен."));

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
        Employee employee = userRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Пользователь не существует или некорректен."));
        return EmployeeConverter.toDTO(employee);
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return userRepository.findAll()
                .stream()
                .map(EmployeeConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UUID getEmployeeIdByUsername(String username) {
        Employee employee = userRepository.findUserByUsername(username);
        if (employee == null) {
            return null;
        }
        return employee.getId();
    }

    @Override
    public boolean isEmployeeExistByUsername(String username) {
        return userRepository.findUserByUsername(username) != null;
    }
}
