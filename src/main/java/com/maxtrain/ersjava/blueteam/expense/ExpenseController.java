package com.maxtrain.ersjava.blueteam.expense;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/expenses")
public class ExpenseController {

	private final String Status_APPROVED = "APPROVED";
	private final String Status_REVIEW = "REVIEW";
	private final String Status_REJECT = "REJECTED";
	
	
	@Autowired
	private ExpenseRepository expRepo;
	
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
	
	@GetMapping ("/api/expenses/approved")
	public ResponseEntity<Iterable<Expense>> getExpensesApproved(){
		Iterable<Expense> expensesApproved = expRepo.findByStatus(Status_APPROVED);
		return new ResponseEntity<Iterable<Expense>>(expensesApproved, HttpStatus.OK);
	}
	
	@GetMapping ("/api/expenses/review")
	public ResponseEntity <Iterable<Expense>> getExpensesInReview(){
		Iterable<Expense> expensesInReview = expRepo.findByStatus(Status_REVIEW);
		return new ResponseEntity<Iterable<Expense>>(expensesInReview, HttpStatus.OK);
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

	
	@SuppressWarnings("rawtypes")
	@PutMapping("review/{id}")
	public ResponseEntity reviewExpense(@PathVariable int id, @RequestBody Expense expense) {
		String newExpense = expense.getTotal() <= 75 ? Status_APPROVED : Status_REVIEW;
		expense.setStatus(newExpense);
		return putExpense(id, expense);
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("approve/{id}")
		public ResponseEntity approveExpense(@PathVariable int id, @RequestBody Expense expense) {
		expense.setStatus(Status_APPROVED);
		return putExpense(id, expense);
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("reject/{id}")
	public ResponseEntity rejectExpense(@PathVariable int id, @RequestBody Expense expense) {
		expense.setStatus(Status_REJECT);
		return putExpense(id, expense);
	}
	
	@PostMapping
	public ResponseEntity<Expense> postExpense(@RequestBody Expense expense){
		Expense newExpense = expRepo.save(expense);
		return new ResponseEntity<Expense>(newExpense, HttpStatus.CREATED);
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping
	public ResponseEntity deleteExpense(@PathVariable int id) {
		Optional<Expense> expense = expRepo.findById(id);
		if(expense.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		expRepo.delete(expense.get());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
