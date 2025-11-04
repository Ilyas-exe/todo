package com.todo.todo_service.model;

import jakarta.persistence.*; // Note: We import from jakarta.persistence
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity // 1. Tells Hibernate this class is a database table
@Table(name = "todo_lists") // 2. Gives the table a clean name in PostgreSQL
public class TodoList {

    @Id // 3. Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 4. Tells PostgreSQL to auto-increment this ID
    private Long id;

    private String title;

    // We'll store the ID of the user who owns this list.
    // We'll get this from the JWT token later.
    private String userId;

    // 5. This is the relationship mapping
    @OneToMany(
            mappedBy = "list", // 6. "mappedBy" tells Hibernate: "Look for the 'list' field in the 'Task' class to find the foreign key"
            cascade = CascadeType.ALL, // 7. If we delete a list, delete all its tasks too
            orphanRemoval = true
    )
    private List<Task> tasks = new ArrayList<>();

    // Helper method to add tasks
    public void addTask(Task task) {
        tasks.add(task);
        task.setList(this);
    }
}