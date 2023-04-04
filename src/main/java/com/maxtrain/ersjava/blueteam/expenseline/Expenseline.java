package com.maxtrain.ersjava.blueteam.expenseline;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.maxtrain.ersjava.blueteam.expense.Expense;
import com.maxtrain.ersjava.blueteam.item.Item;

import jakarta.persistence.*;


@Entity
@Table(name="expenselines")
public class Expenseline {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private int quantity = 1;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="itemId", columnDefinition="int")
	private Item item;
	
	@JsonBackReference
	@ManyToOne(optional=false)
	@JoinColumn(name="expenseId", columnDefinition="int")
	private Expense expense;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		if(quantity < 1) {
			throw new IllegalArgumentException();
		}
		this.quantity = quantity;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public Expense getExpense() {
		return expense;
	}
	public void setExpense(Expense expense) {
		this.expense = expense;
	}
	
	public Expenseline () {}
	
}
