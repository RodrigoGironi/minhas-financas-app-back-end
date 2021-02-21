package com.gbarroso.minhasfinancas.model.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gbarroso.minhasfinancas.model.entity.Usuario;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		//cenario
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@gmail.com").build();
		repository.save(usuario);
		
		//acao | execução
		boolean result = repository.existsByEmail("usuario@gmail.com");
		
		//verificação
		Assertions.assertThat(result).isTrue();
		
	}
	
	@Test
	public void deveVerificarANaoExistenciaDeUmEmail() {
		
		repository.deleteAll();
		boolean result = repository.existsByEmail("usuario@gmail.com");
		//verificação
		Assertions.assertThat(result).isFalse();
		
	}
	
}
