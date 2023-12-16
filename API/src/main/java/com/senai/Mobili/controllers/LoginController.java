package com.senai.Mobili.controllers;

import com.senai.Mobili.Services.TokenService;
import com.senai.Mobili.dtos.LoginDto;
import com.senai.Mobili.dtos.TokenDto;
import com.senai.Mobili.models.UsuarioModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto dadosLogin) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dadosLogin.email(), dadosLogin.senha());
        var auth = authenticationManager.authenticate(usernamePassword);
        var token = tokenService.gerarToken((UsuarioModel) auth.getPrincipal());
        return ResponseEntity.status(HttpStatus.OK).body(new TokenDto(token));
    }
}
