package com.maxtrain.ersjava.blueteam.employee;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository <Employee, Integer>{
	Optional<Employee> findByEmailAndPassword(String email, String password);
}
