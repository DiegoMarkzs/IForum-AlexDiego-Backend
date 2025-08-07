package com.projeto.IForum.dto;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;


@Data
public class RelatoDTO {

    private String tipoRelato;
    private String categoria;
    private String assunto;
    private boolean anonimo;
    private String status;
    private Long userId;

    private MultipartFile arquivo;

}
