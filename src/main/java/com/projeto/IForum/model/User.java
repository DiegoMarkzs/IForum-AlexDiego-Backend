package com.projeto.IForum.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.InheritanceType;
import lombok.Data;

@Entity
@Table(name = "TB_USER")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class User implements Serializable {
	private static final long serialVersionUID = -6518853480190451215L;
	

	
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
@JsonManagedReference
private List<Relato> relatos;
		

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	@SequenceGenerator(name = "user_seq", sequenceName = "tb_user_seq", allocationSize = 1)
	private Long id;

	@Column(unique = true)
	@Nullable()
	private String email;
	
	@Column(name = "Nome")
	private String nome;
	
	@Column(name = "Sobrenome")
	private String sobrenome;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "Nascimento")
	private Date nascimento;
	
	@Column(name = "Senha")
	private String senha;
	
	public User() {
		
	}	

	


}
