package com.example.contactmanager.Repositories;

import com.example.contactmanager.Model.ContactDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactDetailsRepository extends JpaRepository<ContactDetails, Long>
{
    public Optional<ContactDetails> findByFirstName(String firstname);
}
