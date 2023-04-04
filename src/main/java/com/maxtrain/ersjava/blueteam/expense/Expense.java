package com.maxtrain.ersjava.blueteam.expense;

import com.maxtrain.ersjava.blueteam.employee.*;
import com.maxtrain.ersjava.blueteam.expenseline.*;
import jakarta.persistence.*;

@Entity
@Table(name="Expenses")
public class Expense {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(length=80, nullable=false)
	private String description;
	@Column(length=10, nullable=false)
	private String status="NEW";
	@Column(columnDefinition="decimal(11,2) NOT NULL DEFAUTL 0")
	private double total;
	
	// FK's and Virtual Properties
	// @ManyToOne(optional=false)
	// @JoinColumn(name="employeeId", columnDefinition="int")
	// private Employee employee;
	//
	// @JsonManagedReference
	// @OneToMany(mappedBy="expense")
	// private List<Expenseline> expenslines;
	
	
	
	//Getters and Setters	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	

	
}
