package com.shepherdmoney.interviewproject.repository;

import com.shepherdmoney.interviewproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;



/**
 * Crud Repository to store User classes
 */
@Repository("UserRepo")
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findById(String userId);

    public void deleteById(int userId);


}
