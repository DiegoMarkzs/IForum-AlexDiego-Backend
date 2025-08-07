package com.projeto.IForum.model;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "TB_COORDENADOR")
@Data

public class Coordenador extends User{

    
}
