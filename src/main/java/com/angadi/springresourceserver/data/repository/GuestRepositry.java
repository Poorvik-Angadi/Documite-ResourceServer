package com.angadi.springresourceserver.data.repository;

import com.angadi.springresourceserver.data.entities.GDocument;
import com.angadi.springresourceserver.data.entities.Guest;
import com.angadi.springresourceserver.data.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepositry extends JpaRepository<Guest,Long> {

}
