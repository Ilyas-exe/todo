package com.todo.user_service.repository;

import com.todo.user_service.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Tells Spring to manage this as a repository Bean
public interface UserRepository extends MongoRepository<User, String> {

    // Spring Data MongoDB will automatically create a query from this method name
    // It will look in the "users" collection for a document with a matching email
    Optional<User> findByEmail(String email);
}