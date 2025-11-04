package com.todo.todo_service.service;

import com.todo.todo_service.model.TodoList;
import com.todo.todo_service.repository.TodoListRepository;
import com.todo.todo_service.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 1. Tells Spring this is a Service Bean (IoC)
public class TodoService {

    // --- Dependencies ---
    private final TodoListRepository todoListRepository;
    private final TaskRepository taskRepository;

    // --- Dependency Injection ---
    // 2. We use constructor injection to get our repositories
    @Autowired
    public TodoService(TodoListRepository todoListRepository, TaskRepository taskRepository) {
        this.todoListRepository = todoListRepository;
        this.taskRepository = taskRepository;
    }

    // --- Business Logic ---

    /**
     * Creates a new TodoList and assigns it to a user.
     * @param todoList The new list to create (comes from the request)
     * @param userId The ID of the user (comes from the JWT token)
     * @return The saved TodoList entity
     */
    public TodoList createTodoList(TodoList todoList, String userId) {
        // 3. We set the owner of this list
        todoList.setUserId(userId);
        
        // 4. We save the new list to the database
        return todoListRepository.save(todoList);
    }

    /**
     * Finds all TodoLists owned by a specific user.
     * @param userId The ID of the user (comes from the JWT token)
     * @return A list of their TodoLists
     */
    public List<TodoList> getListsForUser(String userId) {
        // 5. We use the custom query method we defined in our repository!
        return todoListRepository.findByUserId(userId);
    }
    
    // We will add more methods here later (like addTask, updateTask, etc.)
}