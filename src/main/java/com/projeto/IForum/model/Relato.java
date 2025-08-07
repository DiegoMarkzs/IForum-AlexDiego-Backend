package com.projeto.IForum.model;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.InheritanceType;


@Entity
@Table(name = "TB_RELATO")
@Inheritance(strategy = InheritanceType.JOINED)
@Data

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "tipoRelato" 
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = RelatoAnonimo.class, name = "Anonima"),
  @JsonSubTypes.Type(value = RelatoPublico.class, name = "Publica")
})

public abstract class Relato {

  @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "usuario_id")
@JsonIgnoreProperties({"relatos", "senha", "email", "tipo", "setor", "curso"})
private User usuario;

    public void setUsuario(User usuario){
        this.usuario = usuario;
    }


    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Enumerated(EnumType.STRING)
    private CategoriaRelato categoria;    
    
	@Column(name = "data")
    private LocalDate data;

    @Column(name = "assunto")
    private String assunto;

    @Column(name = "tipo_de_relato")
    @Enumerated(EnumType.STRING)
    private TipoRelato tipo;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusRelato status = StatusRelato.PENDENTE;

    @Column(name = "arquivo_url")
    private String arquivoUrl;



    

}


