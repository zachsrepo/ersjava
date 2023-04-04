package com.maxtrain.ersjava.blueteam.expenseline;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maxtrain.ersjava.blueteam.expense.Expense;
import com.maxtrain.ersjava.blueteam.expense.ExpenseRepository;
import com.maxtrain.ersjava.blueteam.item.Item;
import com.maxtrain.ersjava.blueteam.item.ItemRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/expenselines")
public class ExpenselineController {
	@Autowired
	public ExpenselineRepository exlRepo;
	@Autowired
	public ExpenseRepository expRepo;
	@Autowired
	public ItemRepository itemRepo;
	
	@GetMapping
	public ResponseEntity<Iterable<Expenseline>> getExpenselines(){
		Iterable<Expenseline> expenselines = exlRepo.findAll();
		return new ResponseEntity< Iterable<Expenseline>>(expenselines, HttpStatus.OK);
	}
	@GetMapping("{id}")
	public ResponseEntity<Expenseline> getExpenseline(@PathVariable int id){
		Optional<Expenseline> expenseline = exlRepo.findById(id);
		if(expenseline.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Expenseline>(expenseline.get(), HttpStatus.OK);
	}
	@PostMapping
	public ResponseEntity<Expenseline> postExpenseline(@RequestBody Expenseline expenseline){
		Expenseline newExpenseline = exlRepo.save(expenseline);
		exlRepo.findById(newExpenseline.getId());
		Optional<Expense> expense = expRepo.findById(expenseline.getExpense().getId());
		if(!expense.isEmpty()) {
			boolean success = recalculateExpenseTotal(expense.get().getId());
			if(!success) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<Expenseline>(newExpenseline, HttpStatus.OK);
	}
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity putExpenseline (@PathVariable int id, @RequestBody Expenseline expenseline) {
		if(expenseline.getId() != id) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		exlRepo.save(expenseline);
		Optional<Expense> expense = expRepo.findById(expenseline.getExpense().getId());
		if(!expense.isEmpty()) {
			boolean success = recalculateExpenseTotal(expense.get().getId());
			if(!success) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	@SuppressWarnings("rawtypes")
	@DeleteMapping("{id}")
	public ResponseEntity deleteExpenseline(@PathVariable int id) {
		Optional<Expenseline> expenseline = exlRepo.findById(id);
		if(expenseline.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		exlRepo.delete(expenseline.get());
		Optional<Expense> expense = expRepo.findById(expenseline.get().getExpense().getId());
		if(!expense.isEmpty()) {
			boolean success = recalculateExpenseTotal(expense.get().getId());
			if(!success) {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	private boolean recalculateExpenseTotal(int expenseId) {
		Optional<Expense> aExpense = expRepo.findById(expenseId);
		if(aExpense.isEmpty()) {
			return false;
		}
		Expense expense = aExpense.get();
		Iterable<Expenseline> expenselines = exlRepo.findByExpenseId(expenseId);
		double total = 0;
		for(Expenseline el : expenselines) {
			if(el.getItem().getName() == null) {
				Item item = itemRepo.findById(el.getItem().getId()).get();
				el.setItem(item);
			}
			total += el.getQuantity() * el.getItem().getPrice();
		}
		expense.setTotal(total);
		expRepo.save(expense);
		return true;
	}
}
