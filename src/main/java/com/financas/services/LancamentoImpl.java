package com.financas.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financas.enums.StatusLancamento;
import com.financas.model.entity.Lancamento;
import com.financas.repositories.LancamentoRepository;
import com.financas.services.exceptions.RegraNegocioException;

@Service
public class LancamentoImpl implements LancamentoService{

	@Autowired
	LancamentoRepository repository;
	
	public LancamentoImpl(LancamentoRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
    @Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		Example example = Example.of( lancamentoFiltro, ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING) );
		
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("informe uma descricao valida.");
		}
		if(lancamento.getMes() == null || lancamento.getMes() < 1 ||lancamento.getMes() > 12 ) {
			throw new  RegraNegocioException("Informe um mes valido.");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ) {
			throw new RegraNegocioException("Informe um Ano valido.");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null ) {
			throw new RegraNegocioException("Informe o usuario");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um valor valido.");
		}
		if(lancamento.getTipo() == null ) {
			throw new RegraNegocioException("Informe um tipo de lancamento.");
		}
	}

}
