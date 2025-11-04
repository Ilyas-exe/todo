package com.todo.todo_service.controller;

import com.todo.todo_service.model.TodoList;
import com.todo.todo_service.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // 1. We'll need this
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists") // 2. Base URL for all todo-list endpoints
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * Endpoint to create a new TodoList
     */
    @PostMapping
    public ResponseEntity<TodoList> createTodoList(@RequestBody TodoList todoList, Authentication authentication) {
        // 3. We get the user's ID (which is their email) from the Authentication object
        String userId = authentication.getName(); 
        
        TodoList savedList = todoService.createTodoList(todoList, userId);
        return new ResponseEntity<>(savedList, HttpStatus.CREATED);
    }

    /**
     * Endpoint to get all TodoLists for the logged-in user
     */
    @GetMapping
    public ResponseEntity<List<TodoList>> getMyTodoLists(Authentication authentication) {
        // 4. We use the same Authentication object to find their lists
        String userId = authentication.getName();
        
        List<TodoList> myLists = todoService.getListsForUser(userId);
        return ResponseEntity.ok(myLists);
    }
}