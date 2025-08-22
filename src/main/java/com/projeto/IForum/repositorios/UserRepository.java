package com.projeto.IForum.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projeto.IForum.model.Aluno;
import com.projeto.IForum.model.Coordenador;
import com.projeto.IForum.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByEmailAndSenha(String email, String senha);

    User findByEmail(String email);

    @Query("SELECT a FROM Aluno a WHERE a.matricula = :matricula")
    Optional<Aluno> findAlunoByMatricula(@Param("matricula") String matricula);

    @Query("SELECT c FROM Coordenador c WHERE c.matricula = :matricula")
    Optional<Coordenador> findCoordenadorByMatricula(@Param("matricula") String matricula);
}
