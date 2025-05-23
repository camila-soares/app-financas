package com.financas.controllers;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financas.dto.AtualizaStatusDTO;
import com.financas.dto.LancamentoDTO;
import com.financas.enums.StatusLancamento;
import com.financas.enums.TipoLancamento;
import com.financas.model.entity.Lancamento;
import com.financas.model.entity.Usuario;
import com.financas.services.LancamentoService;
import com.financas.services.UsuarioService;
import com.financas.services.exceptions.RegraNegocioException;

import lombok.RequiredArgsConstructor;


@CrossOrigin(origins= "http://localhost:3000")
@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

	@Autowired
	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	
	
	@GetMapping
	public ResponseEntity buscar(
			
	@RequestParam(value = "descricao", required = false)String descricao,
	@RequestParam(value = "mes", required = false)Integer mes, 
	@RequestParam(value = "ano", required = false) Integer ano,
	@RequestParam(value = "tipo",required = false) TipoLancamento tipo,
	@RequestParam("usuario") Long idusuario) {
		
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);
		lancamentoFiltro.setTipo(tipo);
		
		Optional<Usuario> usuario = usuarioService.obterPorId(idusuario);
		if(!usuario.isPresent()) {
			return ResponseEntity.badRequest().body("Nao foi possivel realizar a consulta, Usuario nao encontrado para o id informado");
		}else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		return ResponseEntity.ok(lancamentos);
		
	}
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO stausDto) {
		return service.obterPorId(id).map(entity -> {
		StatusLancamento statusSelecioando	=StatusLancamento.valueOf(stausDto.getStatus());
		if(statusSelecioando == null) {
			return ResponseEntity.badRequest().body("nao foi possivel atualizar o status de lancamento, envie um status valido.");
		}
		try {
		entity.setStatus(statusSelecioando);
		service.atualizar(entity);
		return ResponseEntity.ok(entity);
		}catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}).orElseGet(() -> new ResponseEntity("Lancamento nao encontrado na base de dados.", HttpStatus.BAD_REQUEST));
			
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
		Lancamento entidade = converter(dto);
		entidade = service.salvar(entidade);
		return new ResponseEntity(entidade, HttpStatus.CREATED);
		}catch (RegraNegocioException e) {
			return ResponseEntity.badRequest()
					.body(e.getMessage());
		}
	}
	

	@GetMapping("{id}")
	public ResponseEntity findById(@PathVariable("id") Long id) {
		return service.buscaPorId(id)
				.map( lancamento -> new ResponseEntity<LancamentoDTO>(converter(lancamento), HttpStatus.OK))
				.orElseGet( () -> new ResponseEntity(HttpStatus.OK));
	}
	
	@PutMapping("{id}")
	public ResponseEntity autualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		return service.obterPorId(id).map(entity -> {
			try {
			Lancamento lancamento = converter(dto);
			lancamento.setId(entity.getId());
			service.atualizar(lancamento);
			return ResponseEntity.ok(lancamento);
			}catch (RegraNegocioException e) {
				return ResponseEntity.badRequest()
						.body(e.getMessage());
			}
			}).orElseGet(()-> 
		new ResponseEntity("lancamento nao encontrado na base de dados.", HttpStatus.BAD_REQUEST));
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entidade -> {
			service.deletar(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() ->
		new ResponseEntity("Lancamento nao encontrado na bade de dados.", HttpStatus.BAD_REQUEST));
	}
	
	
	
	private LancamentoDTO converter(Lancamento lancamento) {
	return LancamentoDTO.builder()
			.id(lancamento.getId())
			.descricao(lancamento.getDescricao())
			.valor(lancamento.getValor())
			.vencimento(lancamento.getVencimento())
			.mes(lancamento.getMes())
			.ano(lancamento.getAno())
			.status(lancamento.getStatus().name())
			.tipo(lancamento.getTipo().name())
			.usuario(lancamento.getUsuario().getId())
			.build();
		
	}
	
	private Lancamento converter (LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setVencimento(dto.getVencimento());
		
		Usuario usuario = usuarioService
		.obterPorId(dto.getUsuario())
		.orElseThrow(() -> new RegraNegocioException("Usuario nao encontrado para o Id informado."));
		
		lancamento.setUsuario(usuario);
		if(dto.getTipo() != null) {
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if(dto.getStatus() != null) {
		lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		return lancamento;
	}
}
