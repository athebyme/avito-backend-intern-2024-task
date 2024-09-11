package io.codefresh.gradleexample.dao.dto;

import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidIdException;
import io.codefresh.gradleexample.exceptions.dto_exceptions.InvalidUsernameException;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmployeeDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;

    public EmployeeDTO(UUID id, String username, String firstName, String lastName) {
        if (id == null) {
            throw new InvalidIdException("User id cant be null !");
        }
        if (username == null || firstName == null || lastName == null) {
            throw new InvalidUsernameException("Username cant be null !");
        }

        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
