package com.financas.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.financas.enums.StatusLancamento;
import com.financas.model.entity.Lancamento;
import com.financas.model.entity.Usuario;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);

	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar( Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento);
	
	void validar(Lancamento lancamento);
	
	Optional<Lancamento> obterPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);

	Optional<Usuario> findById(Long id);
	
	Optional<Lancamento> buscaPorId(Long id);
}
