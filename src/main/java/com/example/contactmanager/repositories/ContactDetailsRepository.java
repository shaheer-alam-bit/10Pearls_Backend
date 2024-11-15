package com.example.contactmanager.repositories;

import com.example.contactmanager.model.ContactDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactDetailsRepository extends JpaRepository<ContactDetails, Long>
{
    public List<ContactDetails> findByUserIdAndFirstNameContainingIgnoreCase(long userId,String firstname);

    public Page<ContactDetails> findByUserId(Long userId, Pageable pageable);
}
