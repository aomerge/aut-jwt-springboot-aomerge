package com.aut_jwt.aut_jwt.services;

import com.aut_jwt.aut_jwt.config.validator.BaseAuthValid;
import com.aut_jwt.aut_jwt.dto.AuthServideDto;
import com.aut_jwt.aut_jwt.entity.Role;
import com.aut_jwt.aut_jwt.entity.RolesUsers;
import com.aut_jwt.aut_jwt.entity.Users;
import com.aut_jwt.aut_jwt.repository.RoleRepository;
import com.aut_jwt.aut_jwt.repository.RolesUsersRepository;
import com.aut_jwt.aut_jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthServideDto {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesUsersRepository rolesUsersRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void AuthUser() {
    }

    @Override
    public BaseAuthValid register(BaseAuthValid user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        Users newUser = new Users();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encodedPassword);

        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) throw new IllegalArgumentException("Username already exists");

        Users saveUser = userRepository.save(newUser);
        assignDefaultRoleToUser(saveUser);

        return user;
    }

    private void assignDefaultRoleToUser(Users user) {
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));
        Role ownerRole = roleRepository.findByName("OWNER")
                .orElseThrow(() -> new RuntimeException("Role OWNER not found"));

        if (userRepository.count() == 1) {
            // If this is the first user, assign both USER and OWNER roles
            RolesUsers ownerRoleUser = new RolesUsers();
            ownerRoleUser.setUser(user);
            ownerRoleUser.setRole(ownerRole);
            rolesUsersRepository.save(ownerRoleUser);
        }

        RolesUsers roleUser = new RolesUsers();
        roleUser.setUser(user);
        roleUser.setRole(defaultRole);
        rolesUsersRepository.save(roleUser);
    }

}
