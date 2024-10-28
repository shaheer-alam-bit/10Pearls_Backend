package com.example.contactmanager.Repositories;

import com.example.contactmanager.Model.ContactDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactDetailsRepository extends JpaRepository<ContactDetails, Long>
{
    public List<ContactDetails> findByFirstNameContainingIgnoreCase(String firstname);

    public Page<ContactDetails> findByUserId(Long userId, Pageable pageable);
}
