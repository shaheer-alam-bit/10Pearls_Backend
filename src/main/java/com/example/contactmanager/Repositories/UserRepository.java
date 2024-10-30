package com.example.contactmanager.Repositories;

import com.example.contactmanager.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    public Optional<User> findByEmail(String email);
}
