package com.todo.todo_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private boolean completed = false; // Default to not completed

    // 8. This is the "owning" side of the relationship
    @ManyToOne(fetch = FetchType.LAZY) // 9. Many Tasks belong to One List. LAZY = don't load the list unless we ask for it
    @JoinColumn(name = "list_id") // 10. This creates a column in the "tasks" table named "list_id" that stores the foreign key
    private TodoList list;

    // We override toString to prevent infinite loops when logging
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", completed=" + completed +
                '}';
    }
}