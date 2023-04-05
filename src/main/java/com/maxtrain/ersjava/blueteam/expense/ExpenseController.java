package com.maxtrain.ersjava.blueteam.expense;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maxtrain.ersjava.blueteam.employee.*;

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
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// ***********Additional Pay Expense Method***********
	@SuppressWarnings("rawtypes")
	@PutMapping("pay/{expenseId}")
	public ResponseEntity payExpense(@PathVariable int expenseId) {
		Optional<Expense> expense = expRepo.findById(expenseId);
		if(expense.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Expense paidExpense = expense.get();
		Employee paidEmployee = paidExpense.getEmployee();
		paidExpense.setStatus("PAID");
		paidEmployee.setExpensesPaid(paidEmployee.getExpensesPaid() + paidExpense.getTotal());
		paidEmployee.setExpensesDue(paidEmployee.getExpensesDue() - paidExpense.getTotal());
		expRepo.save(paidExpense);
		empRepo.save(paidEmployee);
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	// ***********Additional Pay Expense Method Version 2***********
	@SuppressWarnings("rawtypes")
	@PutMapping("payv2/{expenseId}")
	public ResponseEntity payExpenseV2(@PathVariable int expenseId) {
		Optional<Expense> expense = expRepo.findById(expenseId);
		if(expense.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Expense paidExpense = expense.get();
		Employee paidEmployee = paidExpense.getEmployee();
		if(paidExpense.getStatus().equals("PAID")) {
			return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);
		}
		paidExpense.setStatus("PAID");
		paidEmployee.setExpensesPaid(paidEmployee.getExpensesPaid() + paidExpense.getTotal());
		paidEmployee.setExpensesDue(paidEmployee.getExpensesDue() - paidExpense.getTotal());
		expRepo.save(paidExpense);
		empRepo.save(paidEmployee);
		return new ResponseEntity<>(HttpStatus.OK);
		
	}
	
	@PostMapping
	public ResponseEntity<Expense> postExpense(@RequestBody Expense expense){
		Expense newExpense = expRepo.save(expense);
		return new ResponseEntity<Expense>(newExpense, HttpStatus.CREATED);
	}
	
	// Note from the PayExpenseMethodJW branch
	@SuppressWarnings("rawtypes")
	@DeleteMapping("{id}")
	public ResponseEntity deleteExpense(@PathVariable int id) {
		Optional<Expense> expense = expRepo.findById(id);
		if(expense.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		expRepo.delete(expense.get());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
