package com.projeto.IForum.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.projeto.IForum.dto.UserDTO;
import com.projeto.IForum.model.Aluno;
import com.projeto.IForum.model.Coordenador;
import com.projeto.IForum.model.Funcionario;
import com.projeto.IForum.model.User;
import com.projeto.IForum.service.CriptografiaService;
import com.projeto.IForum.service.JwtService;
import com.projeto.IForum.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/suap")

class SuapController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtUtils;

    @Autowired
    private CriptografiaService criptografia;

    @PostMapping("/auth")
    public ResponseEntity<?> autenticarSuap(@RequestBody Map<String, String> dados) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://suap.ifpb.edu.br/api/jwt/obtain_token/";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(dados, headers);

            ResponseEntity<Map> resposta = restTemplate.postForEntity(url, request, Map.class);
            return ResponseEntity.ok(resposta.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro SUAP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro ao autenticar no SUAP: " + e.getMessage());
        }
    }

    @PostMapping("/login")
public ResponseEntity<?> loginSuap(@RequestBody Map<String, String> dados) {
    String matricula = dados.get("username");
    String senha = dados.get("password");

    Optional<User> optionalUser = userService.buscarPorMatricula(matricula);
    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Usuário não encontrado");
    }

    User user = optionalUser.get();

    if (!criptografia.verificarSenha(senha, user.getSenha())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Senha incorreta");
    }

    String tokenInterno = jwtUtils.generateToken(user.getId());
    return ResponseEntity.ok(gerarRespostaUser(user, tokenInterno));
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