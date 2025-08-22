package com.projeto.IForum.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


import com.projeto.IForum.dto.UserDTO;
import com.projeto.IForum.model.Aluno;
import com.projeto.IForum.model.Coordenador;
import com.projeto.IForum.model.Funcionario;
import com.projeto.IForum.model.User;
import com.projeto.IForum.service.CriptografiaService;

import com.projeto.IForum.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/usuario")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CriptografiaService cript;


    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody UserDTO dto) {
        User user;

        switch (dto.getTipoUsuario().toUpperCase()) {
            case "FUNCIONARIO":
                Funcionario f = new Funcionario();
                f.setSetor(dto.getSetor());
                user = f;
                break;
            case "ALUNO":
                Aluno a = new Aluno();
                a.setCurso(dto.getCurso());
                a.setMatricula(dto.getMatricula());
                user = a;
                break;
            case "COORDENADOR":
                Coordenador c = new Coordenador();
                c.setMatricula(dto.getMatricula());
                user = c;
                break;
            default:
                return ResponseEntity.badRequest().body("Tipo de usuário inválido");
        }

        user.setEmail(dto.getEmail());
        user.setNome(dto.getNome());
        user.setSobrenome(dto.getSobrenome());
        user.setNascimento(dto.getNascimento());

        String senhaCriptografada = cript.criptografar(dto.getSenha());
        user.setSenha(senhaCriptografada);

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
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        User user = optionalUser.get();
        user.setNome(dto.getNome());
        user.setSobrenome(dto.getSobrenome());
        user.setNascimento(dto.getNascimento());

        if (user instanceof Funcionario && dto.getSetor() != null)
            ((Funcionario) user).setSetor(dto.getSetor());

        if (user instanceof Aluno && dto.getCurso() != null)
            ((Aluno) user).setCurso(dto.getCurso());

        User atualizado = userService.salvar(user);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (userService.buscarPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        userService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> listarTodos() {
        return ResponseEntity.ok(userService.listarTodos());
    }

    @GetMapping("/temUsuarios")
    public ResponseEntity<Boolean> temUsuarios() {
        boolean existe = !userService.listarTodos().isEmpty();
        return ResponseEntity.ok(existe);
    }
    
}
