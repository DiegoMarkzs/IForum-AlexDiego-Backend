package com.projeto.IForum.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projeto.IForum.dto.UserDTO;
import com.projeto.IForum.model.Aluno;
import com.projeto.IForum.model.Coordenador;
import com.projeto.IForum.model.Funcionario;
import com.projeto.IForum.model.User;
import com.projeto.IForum.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginDTO) {
        User user = userService.buscarPorEmail(loginDTO.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
        }

        if (!user.getSenha().equals(loginDTO.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        }

        UserDTO response = new UserDTO();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setNome(user.getNome());
        response.setSobrenome(user.getSobrenome());
        response.setNascimento(user.getNascimento());

        if (user instanceof Coordenador) {
            response.setTipoUsuario("COORDENADOR");
        } else if (user instanceof Funcionario) {
            response.setTipoUsuario("FUNCIONARIO");
            response.setSetor(((Funcionario) user).getSetor());
        } else if (user instanceof Aluno) {
            response.setTipoUsuario("ALUNO");
            response.setCurso(((Aluno) user).getCurso());
        } else {
            response.setTipoUsuario("DESCONHECIDO");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody UserDTO dto) {
        User user;

        switch(dto.getTipoUsuario().toUpperCase()) {
            case "FUNCIONARIO":
                Funcionario f = new Funcionario();
                f.setSetor(dto.getSetor());
                user = f;
                break;
            case "ALUNO":
                Aluno a = new Aluno();
                a.setCurso(dto.getCurso());
                user = a;
                break;
            case "COORDENADOR":
                user = new Coordenador();
                break;
            default:
                return ResponseEntity.badRequest().body("Tipo de usuário inválido");
        }

        user.setEmail(dto.getEmail());
        user.setNome(dto.getNome());
        user.setSobrenome(dto.getSobrenome());
        user.setNascimento(dto.getNascimento());
        user.setSenha(dto.getSenha());

        User salvo = userService.salvar(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.buscarPorId(id);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody UserDTO dto) {
        Optional<User> optionalUser = userService.buscarPorId(id);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        user.setNome(dto.getNome());
        user.setSobrenome(dto.getSobrenome());
        user.setNascimento(dto.getNascimento());

       
        if (user instanceof Funcionario && dto.getSetor() != null) {
            ((Funcionario) user).setSetor(dto.getSetor());
        }

        if (user instanceof Aluno && dto.getCurso() != null) {
            ((Aluno) user).setCurso(dto.getCurso());
        }

        User atualizado = userService.salvar(user);
        return ResponseEntity.ok(atualizado);
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (userService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> listarTodos() {
        List<User> usuarios = userService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
}
