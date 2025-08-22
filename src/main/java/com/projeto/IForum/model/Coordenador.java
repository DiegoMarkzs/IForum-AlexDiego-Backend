package com.projeto.IForum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "TB_COORDENADOR")
@Data

public class Coordenador extends User{

    @Column(name = "matricula", unique = true, nullable = false)
    private String matricula;

    
}
