package com.projeto.IForum.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projeto.IForum.model.Relato;
import com.projeto.IForum.model.StatusRelato;
import com.projeto.IForum.model.TipoRelato;

@Repository
public interface RelatoRepository extends JpaRepository<Relato, Long> {
    List<Relato> findByUsuarioId(Long usuarioId);


    long countByStatus(StatusRelato status);
    long countByTipo(TipoRelato tipo);

}
