package hu.pte.ttk.vaadin.cars.user.entity;

import hu.pte.ttk.vaadin.cars.core.entity.CoreEntity;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

@NamedQuery(name = UserEntity.FIND_USER_BY_USERNAME, query = "SELECT u FROM UserEntity u where u.username=:username")
@Table(name = "app_user")
@Entity
public class UserEntity extends CoreEntity implements UserDetails {
    public static final String FIND_USER_BY_USERNAME = "UserEntity.findUserByUsername";

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> authorities;

    @Override
    public Set<RoleEntity> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<RoleEntity> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
