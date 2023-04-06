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

	private final String Status_APPROVED = "APPROVED";
	private final String Status_REVIEW = "REVIEW";
	private final String Status_REJECT = "REJECTED";
	
	
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

	
	@GetMapping ("approved")
	public ResponseEntity<Iterable<Expense>> getExpensesApproved(){
		Iterable<Expense> expensesApproved = expRepo.findByStatus(Status_APPROVED);
		return new ResponseEntity<Iterable<Expense>>(expensesApproved, HttpStatus.OK);
	}
	
	@GetMapping ("review")
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
		Optional<Employee> employee = empRepo.findById(expense.getEmployee().getId());
		boolean success = updateEmployeeExpensesDueAndPaid(employee.get().getId());
		if(!success) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
		// Logic to check current Status of Expense	
		if(paidExpense.getStatus().equals("APPROVED")) {
			paidExpense.setStatus("PAID");
			paidEmployee.setExpensesPaid(paidEmployee.getExpensesPaid() + paidExpense.getTotal());
			paidEmployee.setExpensesDue(paidEmployee.getExpensesDue() - paidExpense.getTotal());
			expRepo.save(paidExpense);
			empRepo.save(paidEmployee);
			return new ResponseEntity<>(HttpStatus.OK);
		}else if(paidExpense.getStatus().equals("PAID")) {
			return new ResponseEntity<>(HttpStatus.ALREADY_REPORTED);	
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
		
		
		int i = 1;
		for(Expense exp : expenses) {
			if(i == 1 && (exp.getStatus() == "PAID" || exp.getStatus() == "APPROVED")) {
				employee.setExpensesDue(0);
				employee.setExpensesPaid(0);
			}
			if(exp.getStatus() == "PAID") {
				
				double expensesPaid = employee.getExpensesPaid() + exp.getTotal(); 
				employee.setExpensesPaid(expensesPaid);
			}
			else if(exp.getStatus() == "APPROVED") {
				
				double expensesDue = employee.getExpensesDue() + exp.getTotal();
				employee.setExpensesDue(expensesDue);
			}
			else {
				employee.setExpensesDue(employee.getExpensesDue());
				employee.setExpensesPaid(employee.getExpensesPaid());
			}
			i++;

			
		}
		empRepo.save(employee);
		return true;
	}
	
}
