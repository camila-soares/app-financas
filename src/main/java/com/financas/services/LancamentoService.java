package com.financas.services;

import java.util.List;

import com.financas.enums.StatusLancamento;
import com.financas.model.entity.Lancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);

	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar( Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento);
	
	void validar(Lancamento lancamento);
}
