package com.financas.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import com.financas.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.financas.model.entity.Usuario;
import com.financas.repositories.UsuarioRepository;
import com.financas.services.exceptions.ErroAutenticacao;
import com.financas.services.exceptions.RegraNegocioException;


@Service
public class UsuarioServiceImpl implements UsuarioService {

	private final UsuarioRepository repository;

	@Autowired
	public PasswordEncoder encoder;
	
	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
    }

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuario nao encontrado para o E-mail informado.");
		}

		boolean senhasBstem = encoder.matches(senha, usuario.get().getSenha());

		if (!senhasBstem) {
			throw new ErroAutenticacao("senha invalida.");
		}
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		cripografarSenha(usuario);
		return repository.save(usuario);
	}

	private void cripografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaBCrypt = encoder.encode(senha);
		usuario.setSenha(senhaBCrypt);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException(" ja existe usuario cadastrado com esse email.");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
				return repository.findById(id);
	}

	@Override
	public List<Usuario> findAll(Usuario usuario) {
		return repository.findAll();
	}

	

	

	@Transactional
	public Usuario updatePassWord(Usuario entity) {
		Objects.requireNonNull(entity.getId());
		return repository.save(entity);
	}	
	


}
