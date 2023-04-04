package com.maxtrain.ersjava.blueteam.item;

import jakarta.persistence.*;

@Entity
@Table(name="items")
public class Item {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(length=30, nullable=false)
	private String name;
	@Column(columnDefinition="decimal(11,2) NOT NULL")
	private double price;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	public Item() {}
}
