package com.projeto.IForum.dto;

import lombok.Data;
import java.util.Date;

@Data
public class UserDTO {
    private long Id;
    private String email;
    private String nome;
    private String sobrenome;
    private Date nascimento;
    private String senha;
    private String tipoUsuario;
    
    // opcionais
    private String setor;  // Do funcionario
    private String curso;  // Do aluno
    
}
   
    