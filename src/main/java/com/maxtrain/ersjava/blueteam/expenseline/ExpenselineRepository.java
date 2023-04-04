package com.maxtrain.ersjava.blueteam.expenseline;



import org.springframework.data.repository.CrudRepository;

public interface ExpenselineRepository extends CrudRepository<Expenseline, Integer>	{
	Iterable<Expenseline> findByExpenseId (int expenseId);

}
