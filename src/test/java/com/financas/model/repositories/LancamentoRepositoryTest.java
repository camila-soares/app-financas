package com.financas.model.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.financas.enums.StatusLancamento;
import com.financas.enums.TipoLancamento;
import com.financas.model.entity.Lancamento;
import com.financas.repositories.LancamentoRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save(lancamento);
		
		assertThat(lancamento.getId()).isNotNull();
	}

	
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		
		 lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		 
		 repository.delete(lancamento);
		 
		 Lancamento lancamentoInxistente = entityManager.find(Lancamento.class, lancamento.getId());
	
		 assertThat(lancamentoInxistente).isNull();
	}



	
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		lancamento.setAno(2019);
		lancamento.setDescricao("teste atualizar");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		
		assertThat(lancamentoAtualizado.getAno()).isEqualTo(2019);
		assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("teste atualizar");
		assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
		
	}
	
	@Test
	public void deveBuscarLancamentoPorId() {
		Lancamento lancamento = criarEPersistirUmLancamento();
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
									.ano(2020)
									.mes(5)
									.descricao("lancamentoTest")
									.valor(BigDecimal.valueOf(10))
									.tipo(TipoLancamento.RECEITA)
									.status(StatusLancamento.PENDENTE)
									.build();
	}
	
	private Lancamento criarEPersistirUmLancamento() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}
}
