package com.bolsadeideas.springboot.backend.apirest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsadeideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsadeideas.springboot.backend.apirest.models.service.IClienteService;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente> index(){		
		return clienteService.findAll();
	}
	
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {		
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		
		try {
			cliente = clienteService.findById(id);
		}catch (DataAccessException e ) {	
			response.put("mensaje", "Cliente ID:".concat(id.toString()).concat(" error al realizar la consulta en la BD"));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		
		if (cliente == null) {
			response.put("mensaje", "El cliente ID:".concat(id.toString()).concat(" no existe en la BD"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@RequestBody Cliente cliente) { //@RequestBody porque viene en formato JSon dentro de la petición
		Map<String, Object> response = new HashMap<>();
		Cliente clienteNew = null;
		
		try{
			clienteNew = clienteService.save(cliente);
		}catch(DataAccessException e) {
			response.put("mensaje", " error al realizar el insert en la BD");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		
		response.put("mensaje", "El cliente ha sido creado con exito");
		response.put("cliente", clienteNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}	
	
	@PutMapping("/clientes/{id}")
	public ResponseEntity<?> update(@RequestBody Cliente cliente, @PathVariable Long id) {	//Recibe el cliente que queremos introducir por eso el @RequestBody, porque viene en formato Json		
		
		Map<String, Object> response = new HashMap<>();
		Cliente clienteActualizado = null;
		Cliente clienteActual = null;
		try {
			clienteActual = clienteService.findById(id);
		}catch (DataAccessException e ) {	
			response.put("mensaje", "Cliente ID:".concat(id.toString()).concat(" error al realizar la consulta en la BD"));
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
				
		if (clienteActual == null) {
			response.put("mensaje", "El cliente ID:".concat(id.toString()).concat(" no existe en la BD, por lo tanto no se puede editar"));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		clienteActual.setApellido(cliente.getApellido());
		clienteActual.setNombre(cliente.getNombre());
		clienteActual.setEmail(cliente.getEmail());		
		
		try {
			clienteActualizado = clienteService.save(clienteActual);			
		}catch (DataAccessException e ) {	
			response.put("mensaje", "Error, el cliente no se ha podido actualizar");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido actualizado con exito");
		response.put("cliente", clienteActualizado);
		//Tambien se actualiza con save, si el id es null hace un persist si el id no es null hace internamente un merge y actualiza
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		try {
			clienteService.delete(id);
		}catch (DataAccessException e ) {	
			response.put("mensaje", "Error al eliminar el cliente en la BD");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente ha sido eliminado con exito");
		return new ResponseEntity<Map<String,Object>>(response, HttpStatus.OK);
	}
}
