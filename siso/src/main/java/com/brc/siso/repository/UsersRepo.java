package com.brc.siso.repository;

import com.brc.siso.entity.OurUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers,Integer> {
    Optional<OurUsers> findByEmail(String email);
    /**JWT generates a signature for a logged-in user**/
}
