package com.financas.controller;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financas.controllers.UsuarioController;
import com.financas.dto.UsuarioDTO;
import com.financas.model.entity.Usuario;
import com.financas.services.LancamentoService;
import com.financas.services.UsuarioService;
import com.financas.services.exceptions.ErroAutenticacao;
import com.financas.services.exceptions.RegraNegocioException;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {
	
	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService ancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		//cenario
		String email = "usuario@email.com";
		String senha = "123";
		
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();	
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao everificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
							.post(API.concat("/autenticar") )
							.accept(JSON)
							.contentType(JSON)
							.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk() )
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );
		
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		//cenario
		String email = "usuario@email.com";
		String senha = "123";
		
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();	
		
		
		when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao everificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
							.post(API.concat("/autenticar") )
							.accept(JSON)
							.contentType(JSON)
							.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest() );
		
								
				
				
				;
		
	}
	
	@Test
	public void deveCriarUmUsuario() throws Exception {
		//cenario
		String email = "usuario@email.com";
		String senha = "1234";
		
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();	
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		when(service.salvarUsuario(Mockito.any(Usuario.class)) ).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao everificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
							.post(API )
							.accept(JSON)
							.contentType(JSON)
							.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isCreated() )
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );
		
	}
	
	@Test
	public void deveRetornarBadRequestATentarCriarUmUsuarioInvalido() throws Exception {
		//cenario
		String email = "usuario@email.com";
		String senha = "1234";
		
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();	
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		when(service.salvarUsuario(Mockito.any(Usuario.class)) ).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execucao everificacao
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
							.post(API )
							.accept(JSON)
							.contentType(JSON)
							.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest() );
	}
	

}
