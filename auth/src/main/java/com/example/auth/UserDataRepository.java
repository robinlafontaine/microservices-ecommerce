package com.example.auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Robin Lafontaine
 */
@Repository
public interface UserDataRepository extends JpaRepository<UserData, Long> {

    public List<UserData> findByFirstName(String firstName);

    public List<UserData> findByLastName(String lastName);

    public List<UserData> findByEmail(String email);

    public List<UserData> findByRole(Enum<Roles> role);

}