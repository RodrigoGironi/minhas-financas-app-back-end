package com.gbarroso.minhasfinancas.api.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gbarroso.minhasfinancas.api.dto.UsuarioDTO;
import com.gbarroso.minhasfinancas.exception.ErroAutenticacao;
import com.gbarroso.minhasfinancas.exception.RegraNegocioException;
import com.gbarroso.minhasfinancas.model.entity.Usuario;
import com.gbarroso.minhasfinancas.service.LancamentoService;
import com.gbarroso.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService service;
	private final LancamentoService lancamentoservice;
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
		try 
		{
			Usuario usuarioAutenticar = service.autenticar(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuarioAutenticar);			
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		Usuario usuario = Usuario.builder()
						 .nome(dto.getNome())
						 .email(dto.getEmail())
						 .senha(dto.getSenha())
						 .build();
		try 
		{
			Usuario UsuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(UsuarioSalvo, HttpStatus.CREATED);
			
		}catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = service.ObterUsuarioPorId(id);
		if(!usuario.isPresent()) {
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		
		BigDecimal saldo = lancamentoservice.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
	
}
