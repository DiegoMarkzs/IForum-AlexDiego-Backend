package com.projeto.IForum.controller;

import com.projeto.IForum.dto.UserDTO;
import com.projeto.IForum.model.Aluno;
import com.projeto.IForum.model.Coordenador;
import com.projeto.IForum.model.Funcionario;
import com.projeto.IForum.model.User;
import com.projeto.IForum.service.CriptografiaService;
import com.projeto.IForum.service.JwtService;
import com.projeto.IForum.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtUtilsService;

    @Autowired
    private CriptografiaService cript;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginDTO) {
        User user = userService.buscarPorEmail(loginDTO.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
        }

        if (!cript.verificarSenha(loginDTO.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
        }

        String token = jwtUtilsService.generateToken(user.getId());
        return ResponseEntity.ok(gerarRespostaUser(user, token));
    }


    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ausente ou inválido");
        }

        String token = authHeader.substring(7);
        Long userId;

        try {
            userId = jwtUtilsService.getUserIdFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }

        Optional<User> optionalUser = userService.buscarPorId(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        User user = optionalUser.get();
        return ResponseEntity.ok(gerarRespostaUser(user, token));
    }

    private Map<String, Object> gerarRespostaUser(User user, String token) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setNome(user.getNome());
        dto.setSobrenome(user.getSobrenome());
        dto.setNascimento(user.getNascimento());

        if (user instanceof Coordenador) {
            dto.setTipoUsuario("COORDENADOR");
        } else if (user instanceof Funcionario) {
            dto.setTipoUsuario("FUNCIONARIO");
            dto.setSetor(((Funcionario) user).getSetor());
        } else if (user instanceof Aluno) {
            dto.setTipoUsuario("ALUNO");
            dto.setCurso(((Aluno) user).getCurso());
        } else {
            dto.setTipoUsuario("DESCONHECIDO");
        }

        HashMap<String, Object> retorno = new HashMap<>();
        retorno.put("user", dto);
        retorno.put("token", token);
        return retorno;
    }
}
