package com.gbarroso.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gbarroso.minhasfinancas.exception.ErroAutenticacao;
import com.gbarroso.minhasfinancas.exception.RegraNegocioException;
import com.gbarroso.minhasfinancas.model.entity.Usuario;
import com.gbarroso.minhasfinancas.model.repository.UsuarioRepository;
import com.gbarroso.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository repository;
			
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuario não encontrado");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		
		if(repository.existsByEmail(email)) {
			throw new RegraNegocioException("Já existe um email para esse Usuario.");
		}
		
	}

	@Override
	public Optional<Usuario> ObterUsuarioPorId(Long id) {
				
		return repository.findById(id);
	}

}
