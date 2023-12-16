package com.senai.Mobili.controllers;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.senai.Mobili.dtos.UsuarioDto;
import com.senai.Mobili.models.UsuarioModel;
import com.senai.Mobili.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/usuarios", produces = {"application/json"})
public class UsuarioController {
    @Autowired
    UsuarioRepository usuarioRepository;
    @GetMapping
    public ResponseEntity<List<UsuarioModel>> listarUsuarios(){
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.findAll());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<Object> buscarUsuarioPorId(@PathVariable(value = "idUsuario") UUID id) {
        Optional<UsuarioModel> usuarioBuscado = usuarioRepository.findById(id);

        return usuarioBuscado.<ResponseEntity<Object>>map(usuarioModel ->
                ResponseEntity.status(HttpStatus.OK).body(usuarioModel)).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado"));
    }

    @PostMapping
    public ResponseEntity<Object> criarUsuario(@RequestBody @Valid UsuarioDto usuarioDto) {
        UsuarioModel usuarioModel;

        if (usuarioRepository.findByEmail(usuarioDto.email()) != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email já cadastrado");
        usuarioModel = new UsuarioModel();
        BeanUtils.copyProperties(usuarioDto, usuarioModel);
        String senhaCript = new BCryptPasswordEncoder().encode(usuarioDto.senha());
        usuarioModel.setSenha(senhaCript);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuarioModel));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<Object> editarUsuario(@PathVariable(value = "idUsuario") UUID id, @RequestBody @Valid UsuarioDto usuarioDto) {
        Optional<UsuarioModel> usuarioBuscado;

        usuarioBuscado = usuarioRepository.findById(id);
        if (usuarioBuscado.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado!");
        UsuarioModel usuarioBd = usuarioBuscado.get();
        BeanUtils.copyProperties(usuarioDto, usuarioBd);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioRepository.save(usuarioBd));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Object> deletarUsuario(@PathVariable(value = "idUsuario") UUID id) {
        Optional<UsuarioModel> usuarioBuscado;

        usuarioBuscado = usuarioRepository.findById(id);
        if (usuarioBuscado.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado!");
        usuarioRepository.delete(usuarioBuscado.get());
        return ResponseEntity.status(HttpStatus.OK).body("Usuario deletado com sucesso");
    }
}
