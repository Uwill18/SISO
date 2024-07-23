package com.brc.siso.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "ourusers" )
@Data /*for getters and our setters*/

/* research UserDetails class:
 * Provides core user information.
 * Implementations are not used directly by Spring Security for security purposes. They simply store user information which is later encapsulated into Authentication objects.
 * This allows non-security related user information (such as email addresses, telephone numbers etc) to be stored in a convenient location.
 * Concrete implementations must take particular care to ensure the non-null contract detailed for each method is enforced. See User for a reference implementation (which you might like to extend or use in your code).
 * https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/userdetails/UserDetails.html**/

public class OurUsers implements UserDetails {
    @Id
    @GeneratedValue(strategy = IDENTITY);
    private Integer id;
    private String email;
    private String name;
    private String password;
    private String city;
    private String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    /**email is functioning as a username**/
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false; //return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
