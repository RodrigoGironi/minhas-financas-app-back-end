package com.gbarroso.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.gbarroso.minhasfinancas.model.entity.Lancamento;
import com.gbarroso.minhasfinancas.model.enumerador.StatusLancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancemento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Optional<Lancamento> ObterLancamentoPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
