package com.projeto.IForum.service;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CriptografiaService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  
    public String criptografar(String senha) {
        return passwordEncoder.encode(senha);
    }

    public boolean verificarSenha(String senhaDigitada, String senhaHasheada) {
        return passwordEncoder.matches(senhaDigitada, senhaHasheada);
    }

    

}
