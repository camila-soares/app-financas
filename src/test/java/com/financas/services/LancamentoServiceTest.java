package com.financas.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.repository.support.Repositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.financas.enums.StatusLancamento;
import com.financas.model.entity.Lancamento;
import com.financas.model.repositories.LancamentoRepositoryTest;
import com.financas.repositories.LancamentoRepository;
import com.financas.services.exceptions.RegraNegocioException;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoImpl service;
	
	@MockBean
	LancamentoRepository respoRepository;

	@Test
	public void deveSalvarUmLancamento() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		when(respoRepository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		//execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		//verificacao
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
		
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenario
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);
		//execucao e verificacao
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		verify(respoRepository, never()).save(lancamentoASalvar);
	}
}
