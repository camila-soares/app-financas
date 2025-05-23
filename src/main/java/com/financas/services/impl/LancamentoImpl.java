package com.financas.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.financas.repositories.UsuarioRepository;
import com.financas.services.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financas.enums.StatusLancamento;
import com.financas.enums.TipoLancamento;
import com.financas.model.entity.Lancamento;
import com.financas.model.entity.Usuario;
import com.financas.repositories.LancamentoRepository;
import com.financas.services.exceptions.RegraNegocioException;

import javax.swing.text.DateFormatter;

@Service
public class LancamentoImpl implements LancamentoService {

	@Autowired
	LancamentoRepository repository;
	@Autowired
	UsuarioRepository usuarioRepository;
	
	public LancamentoImpl(LancamentoRepository repository, UsuarioRepository usuarioRepository) {
		this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		if (lancamento.getTipo().equals(TipoLancamento.RECEITA)){
			lancamento.setStatus(StatusLancamento.EFETIVADO);
		}else {
			lancamento.setStatus(StatusLancamento.PENDENTE);
		}
		lancamento.setDataCadastro(LocalDate.now());
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
		Example  example = Example.of( lancamentoFiltro, ExampleMatcher.matching()
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
			throw new RegraNegocioException("Informe o usuario.");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um valor valido.");
		}
		if(lancamento.getTipo() == null ) {
			throw new RegraNegocioException("Informe um tipo de lancamento.");
		}
	}

	@Override
	public Optional<Lancamento> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);
		if(!usuario.isPresent()) {
			throw new RuntimeException("Usuario nao encontrado.");
		}

		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id,
				TipoLancamento.RECEITA,
				StatusLancamento.EFETIVADO);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id,
				TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
		
		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		if(despesas == null ) {
			despesas = BigDecimal.ZERO;
		}
		return receitas.subtract(despesas);
	}

	@Override
	public Optional<Usuario> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Lancamento> buscaPorId(Long id) {
		// TODO Auto-generated method stub
		return repository.findById(id);
	}

}
