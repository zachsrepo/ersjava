package com.maxtrain.ersjava.blueteam.expense;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maxtrain.ersjava.blueteam.employee.Employee;
import com.maxtrain.ersjava.blueteam.employee.EmployeeRepository;

@CrossOrigin
@RestController
@RequestMapping("api/expenses")
public class ExpenseController {

	@Autowired
	private ExpenseRepository expRepo;
	
	@Autowired
	private EmployeeRepository empRepo;
	
	@GetMapping
	public ResponseEntity<Iterable<Expense>> getAllExpenses(){
		Iterable<Expense> expenses = expRepo.findAll();
		return new ResponseEntity<Iterable<Expense>>(expenses, HttpStatus.OK);
	}
	@GetMapping("{id}")
	public ResponseEntity<Expense> getExpense(@PathVariable int id){
		Optional<Expense> expense = expRepo.findById(id);
		if(expense.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Expense>(expense.get(), HttpStatus.OK);
	}
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity putExpense(@PathVariable int id, @RequestBody Expense expense){
		if(expense.getId() != id) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		expRepo.save(expense);
		Optional<Employee> employee = empRepo.findById(expense.getEmployee().getId());
		boolean success = updateEmployeeExpensesDueAndPaid(employee.get().getId());
		if(!success) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	@PostMapping
	public ResponseEntity<Expense> postExpense(@RequestBody Expense expense){
		Expense newExpense = expRepo.save(expense);
		Optional<Employee> employee = empRepo.findById(expense.getEmployee().getId());
		boolean success = updateEmployeeExpensesDueAndPaid(employee.get().getId());
		if(!success) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Expense>(newExpense, HttpStatus.CREATED);
	}
	@SuppressWarnings("rawtypes")
	@DeleteMapping("{id}")
	public ResponseEntity deleteExpense(@PathVariable int id) {
		Optional<Expense> expense = expRepo.findById(id);
		if(expense.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		expRepo.delete(expense.get());
		Optional<Employee> employee = empRepo.findById(expense.get().getEmployee().getId());
		boolean success = updateEmployeeExpensesDueAndPaid(employee.get().getId());
		if(!success) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	private boolean updateEmployeeExpensesDueAndPaid(int employeeId) {
		Optional<Employee> anEmployee = empRepo.findById(employeeId);
		if(anEmployee.isEmpty()) {
			return false;
		}
		Iterable<Expense> expenses = expRepo.findByEmployeeId(employeeId);
		Employee employee = anEmployee.get();
		double expensesDue = 0;
		double expensesPaid = 0;
		for(Expense exp : expenses) {
			if(exp.getStatus() == "PAID") {
				expensesPaid = employee.getExpensesPaid() + exp.getTotal();
			}
			else if(exp.getStatus() == "APPROVED") {
				expensesDue = employee.getExpensesDue() + exp.getTotal();
			}
			else {
				return false;
			}
			
		}
		employee.setExpensesPaid(expensesPaid);
		employee.setExpensesDue(expensesDue);
		empRepo.save(employee);
		return true;
	}
	
}
