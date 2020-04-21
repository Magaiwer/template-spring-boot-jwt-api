package com.serverside.api.dto;

import com.serverside.api.domain.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Magaiver Santos
 */
@Getter
@Setter
public class UserDTO {
    private User user;
    private Set<String> permissions = new HashSet<>();

    public UserDTO(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        authorities.forEach(authority -> permissions.add(authority.getAuthority()));
    }
}
