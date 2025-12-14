package com.angadi.springresourceserver.data.repository;

import com.angadi.springresourceserver.data.entities.Users;
import com.sun.jdi.LongValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, LongValue> {
    Iterable<Users> findUsersByUserName(String userName);
    Iterable<Users> findUsersByEmail(String userName);

}
