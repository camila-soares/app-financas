package com.financas.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.financas.services.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.financas.model.entity.Usuario;
import com.financas.repositories.UsuarioRepository;
import com.financas.services.exceptions.ErroAutenticacao;
import com.financas.services.exceptions.RegraNegocioException;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
    UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;

	
	@Test(expected = Test.None.class)
	public void deveSalvarUsuarioComSucesso() {
		//cenario
		doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.nome("nome")
				.email("email@email.com")
				.senha("senha")
				.build();
		when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
	}
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarusuarioComEmailJaCadastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//acao
		service.salvarUsuario(usuario);
		
		//verificaca0o
		verify(repository, never()).save(usuario);
	}
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenario
		String email = "camila@email.com";
		String senha = "senha1";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(100l).build();
		when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario r = service.autenticar(email, senha);
		
		//verificacao
		Assertions.assertThat(r).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
		//cenario
		when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		//acao
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));
		
		//verificacao
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuario nao encontrado.");
		
	}
	
	@Test(expected = Test.None.class)
	public void devevalidarEmail() {
		//cenario
		when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		//acao
		service.validarEmail("email@eamail.com");
		
	}
	
	@Test(expected = ErroAutenticacao.class)
	public void deveLancarErroQuandoSenhaNaoBater() {
		//cenario
		String senha = "senha10";
		Usuario usuario = Usuario.builder().email("camila@email.com").senha(senha).build();
		when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		//acao'
		service.autenticar("camila@email.com", "12090909093");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		//cenario
		when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		//acao
		service.validarEmail("email1@email.com");
	}
}
