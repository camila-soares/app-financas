package com.financas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.financas.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);

	List<Usuario> findAll(Usuario usuario);

	Usuario updatePassWord(Usuario entity);

	
}
