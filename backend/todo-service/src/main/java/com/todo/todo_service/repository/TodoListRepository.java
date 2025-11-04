package com.todo.todo_service.repository;

import com.todo.todo_service.model.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Tells Spring to manage this as a repository Bean
public interface TodoListRepository extends JpaRepository<TodoList, Long> { // 1.

    // 2. Spring Data JPA will automatically create a query from this method name
    //    It will look for all lists where the 'userId' column matches
    List<TodoList> findByUserId(String userId);
}