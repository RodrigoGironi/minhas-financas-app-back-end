package com.gbarroso.minhasfinancas.api.controller;

import java.util.List;
import java.util.Optional;

import javax.xml.ws.Response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gbarroso.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.gbarroso.minhasfinancas.api.dto.LancamentoDTO;
import com.gbarroso.minhasfinancas.exception.RegraNegocioException;
import com.gbarroso.minhasfinancas.model.entity.Lancamento;
import com.gbarroso.minhasfinancas.model.entity.Usuario;
import com.gbarroso.minhasfinancas.model.enumerador.StatusLancamento;
import com.gbarroso.minhasfinancas.model.enumerador.TipoLancamento;
import com.gbarroso.minhasfinancas.service.LancamentoService;
import com.gbarroso.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value =  "descricao", required = false ) String descricao,
			@RequestParam(value = "mes", required = false ) Integer mes,
			@RequestParam(value = "ano", required = false ) Integer ano,
			@RequestParam(value = "usuario", required = false) Long idusuario ) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		
		Optional<Usuario> usuario = usuarioService.ObterUsuarioPorId(idusuario);
		
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para id informado.");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@GetMapping("{id}")
	public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
		return service
				.ObterLancamentoPorId(id)
				.map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
				.orElseGet( () -> ResponseEntity.badRequest().body("Não foi possivél achar o lançamento."));
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try 
		{
			Lancamento lancamento = converter(dto);
			lancamento = service.salvar(lancamento);
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
			
		}catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.ObterLancamentoPorId(id).map(entity -> {
			
			try 
			{
				Lancamento lancamento = converter(dto);
				lancamento.setId(entity.getId());
				service.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);				
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
			
		}).orElseGet(()-> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		
	return service.ObterLancamentoPorId(id).map(entity -> {
				service.deletar(entity);
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			
		}).orElseGet(()-> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus( @PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		
		return service.ObterLancamentoPorId(id).map(entity -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			if(statusSelecionado == null ) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento.");
			}
			
			try 
			{
				entity.setStatus(statusSelecionado);
				service.atualizar(entity);
				return ResponseEntity.ok(entity);
				
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet(()-> new ResponseEntity("Lançamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	private LancamentoDTO converter(Lancamento lanc) {
		return LancamentoDTO
				.builder()
				.id(lanc.getId())
				.descricao(lanc.getDescricao())
				.valor(lanc.getValor())
				.mes(lanc.getMes())
				.ano(lanc.getAno())
				.status(lanc.getStatus().name())
				.tipo(lanc.getTipo().name())
				.usuario(lanc.getUsuario().getId())
				.build();
	}
	
	private Lancamento converter( LancamentoDTO dto ) {
	
		Lancamento lancamento =  new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setMes(dto.getMes());
		lancamento.setAno(dto.getAno());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService
				          .ObterUsuarioPorId(dto.getUsuario())
				          .orElseThrow(()->new RegraNegocioException("Usuário não encontrado para id informado."));
		lancamento.setUsuario(usuario);
		
		if(dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		
		if(dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		
		
		
		return lancamento;
	}
}
