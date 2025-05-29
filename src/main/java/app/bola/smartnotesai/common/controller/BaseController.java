package app.bola.smartnotesai.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

public interface BaseController<REQ, RES> {
	
	@PostMapping("new")
	ResponseEntity<RES> create(@RequestBody REQ req);
	
	@PutMapping("update")
	<T> ResponseEntity<?> update(@RequestBody T noteRequest);
	
	@GetMapping ("{public-id}")
	ResponseEntity<RES> findByPublicId(@PathVariable("public-id") String parameter);
	
	@DeleteMapping("{public-id}")
	ResponseEntity<?> delete(@PathVariable("public-id") String publicId);
	
	@GetMapping("all")
	ResponseEntity<Collection<RES>> findAll();
}
