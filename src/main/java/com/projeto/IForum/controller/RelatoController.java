package com.projeto.IForum.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.IForum.model.Relato;
import com.projeto.IForum.model.RelatoAnonimo;
import com.projeto.IForum.model.RelatoPublico;
import com.projeto.IForum.model.StatusRelato;
import com.projeto.IForum.dto.RelatoDTO;
import com.projeto.IForum.model.CategoriaRelato;
import com.projeto.IForum.model.TipoRelato;
import com.projeto.IForum.model.User;
import com.projeto.IForum.service.CloudinaryService;
import com.projeto.IForum.service.RelatoService;
import com.projeto.IForum.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/relatos")
public class RelatoController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private RelatoService relatoService;

    @Autowired
private UserService userService;
 

    @PostMapping(value = "/save", consumes = "multipart/form-data")
public ResponseEntity<?> save(@ModelAttribute RelatoDTO dto,
                              @RequestPart(value = "arquivo", required = false) MultipartFile arquivo) {
    Relato relato;

   
    if (dto.isAnonimo()) {
        relato = new RelatoAnonimo();
    } else {
        relato = new RelatoPublico();
        if (dto.getUserId() != null) {
            Optional<User> usuario = userService.buscarPorId(dto.getUserId());
            if (usuario.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário inválido");
            }
            relato.setUsuario(usuario.get());
        }
    }

    
    relato.setAssunto(dto.getAssunto());
    relato.setCategoria(CategoriaRelato.valueOf(dto.getCategoria()));
    relato.setTipo(TipoRelato.valueOf(dto.getTipoRelato()));
    relato.setStatus(StatusRelato.valueOf(dto.getStatus()));

   
    if (arquivo != null && !arquivo.isEmpty()) {
        try {
            String url = cloudinaryService.uploadFile(arquivo.getBytes(), arquivo.getOriginalFilename());
            relato.setArquivoUrl(url);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao fazer upload do arquivo.");
        }
    }

    Relato salvo = relatoService.salvar(relato);
    return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
}

    @GetMapping("/{id}")
    public ResponseEntity<Relato> getRelatoById(@PathVariable Long id) {
        Optional<Relato> relato = relatoService.buscarPorId(id);
        return relato.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }
   @PutMapping("/{id}")
public ResponseEntity<Relato> atualizarRelato(@PathVariable Long id, @RequestBody RelatoDTO dto) {
   
    Relato novoRelato = dto.isAnonimo() ? new RelatoAnonimo() : new RelatoPublico();
    novoRelato.setAssunto(dto.getAssunto());
    novoRelato.setCategoria(CategoriaRelato.valueOf(dto.getCategoria()));
    novoRelato.setTipo(TipoRelato.valueOf(dto.getTipoRelato()));
    novoRelato.setStatus(StatusRelato.valueOf(dto.getStatus())); 
        
    Relato atualizado = relatoService.atualizarRelato(id, novoRelato);
    return ResponseEntity.ok(atualizado);
  
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarRelato(@PathVariable Long id) {
        if (relatoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        relatoService.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Relato>> listarTodos() {
        List<Relato> relatos = relatoService.buscarTodos();
        return ResponseEntity.ok(relatos);
    }

    @GetMapping("/usuario/{id}")
public ResponseEntity<List<Relato>> listarPorUsuario(@PathVariable Long id) {
    List<Relato> relatos = relatoService.listarPorUsuarioId(id);
    return ResponseEntity.ok(relatos);
}


// É a logica do gerar PDF
@GetMapping("/estatisticas")
public ResponseEntity<?> getEstatisticas() {
    long totalRelatos = relatoService.contarTodos();
    long pendentes = relatoService.contarPorStatus(StatusRelato.PENDENTE);
    long aceitos = relatoService.contarPorStatus(StatusRelato.ACEITO);
    long recusados = relatoService.contarPorStatus(StatusRelato.RECUSADO);
    long sugestoes = relatoService.contarPorTipo(TipoRelato.SUGESTAO);
    long denuncias = relatoService.contarPorTipo(TipoRelato.DENUNCIA);

    return ResponseEntity.ok(Map.of(
        "totalRelatos", totalRelatos,
        "pendentes", pendentes,
        "aceitos", aceitos,
        "recusados", recusados,
        "sugestoes", sugestoes,
        "denuncias", denuncias
    ));
}
    

    

    
}
