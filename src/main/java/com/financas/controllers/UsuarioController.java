package com.financas.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.financas.dto.TokenDTO;
import com.financas.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financas.dto.UsuarioDTO;
import com.financas.model.entity.Usuario;
import com.financas.services.LancamentoService;
import com.financas.services.UsuarioService;
import com.financas.services.exceptions.ErroAutenticacao;
import com.financas.services.exceptions.RegraNegocioException;

import lombok.RequiredArgsConstructor;

@RestController
//@CrossOrigin(origins= "http://localhost:3000")
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	@Autowired
	private final UsuarioService service;
	private final LancamentoService lancamentoService;
	private final JwtService jwtService;
	
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
	try {
		Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
		String token = jwtService.gerarToken(usuarioAutenticado);
		TokenDTO tokenDTO = new TokenDTO(usuarioAutenticado.getNome(), token);
		return ResponseEntity.ok(tokenDTO);
	}catch (ErroAutenticacao e) {
		return ResponseEntity.badRequest()
				.body(e.getMessage());
		}
	
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
	
	@GetMapping
	public List<Usuario> findAll(Usuario usuario) {
		return service.findAll(usuario);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Usuario> autualizar(@PathVariable("id") Long id, @RequestBody UsuarioDTO usuDto) {
		return service.obterPorId(id).map(entity -> {
			entity.setSenha(usuDto.getSenha());
			service.updatePassWord(entity);
			return ResponseEntity.ok(entity);
			}).orElseGet(()-> 
		new ResponseEntity("veiculo nao encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
}
