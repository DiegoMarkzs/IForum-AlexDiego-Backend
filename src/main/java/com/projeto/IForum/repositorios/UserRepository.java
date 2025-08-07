package com.projeto.IForum.repositorios;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.projeto.IForum.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    Optional<User> findByEmailAndSenha(String email, String senha);

    

    User findByEmail(String email);
}
