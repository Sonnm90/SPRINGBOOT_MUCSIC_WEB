package com.example.demo.service.user;

import com.example.demo.model.Band;
import com.example.demo.model.Role;
import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAll();
    Optional<User> findByUserId(Long id);
    Optional<User> findByUsernameContaining(String name);
    Optional<User> findByUsername(String name); //Tim kiem User co ton tai trong DB khong?
    Boolean existsByUsername(String username); //username da co trong DB chua, khi tao du lieu
    Boolean existsByEmail(String email); //email da co trong DB chua
    User save(User user);
    boolean checkUser(User userOwner);
    String getUserRole(User user);
}
