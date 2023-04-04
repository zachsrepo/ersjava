package com.maxtrain.ersjava.blueteam.employee;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	
	@Autowired
	public EmployeeRepository empRepo;
	
	@GetMapping
	public ResponseEntity <Iterable<Employee>> getEmployee(){
		Iterable<Employee> employees = empRepo.findAll();
		return new ResponseEntity<Iterable<Employee>>(employees, HttpStatus.OK);
	}
	
	@GetMapping ("{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable int id){
		Optional<Employee> employee = empRepo.findById(id); 
			if(employee.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Employee> (employee.get(), HttpStatus.OK);
		}
	@GetMapping ("{email}/{password}")
	public ResponseEntity<Employee> employeeLogin(@PathVariable String email, @PathVariable String password){
		Optional<Employee> employee = empRepo.findByEmailAndPassword(email, password);
		if(employee.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Employee>(employee.get(), HttpStatus.OK);
	}
	
	
	
	@PostMapping
	public ResponseEntity<Employee> postEmployee(@RequestBody Employee employee){
			Employee newEmployee = empRepo.save(employee);
			return new ResponseEntity<Employee>(newEmployee, HttpStatus.CREATED);		
	}
	
	@PutMapping ("{id}")
	public ResponseEntity<Employee> putEmployee(@PathVariable int id, @RequestBody Employee employee){
		if(employee.getId() != id) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		empRepo.save(employee);
		return new ResponseEntity<Employee>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping ("{id}")
	public ResponseEntity<Employee> deleteEmployee(@PathVariable int id){
		Optional<Employee> employee = empRepo.findById(id);
		if(employee.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		empRepo.delete(employee.get());
		return new ResponseEntity<Employee>(HttpStatus.OK);
	}
}
