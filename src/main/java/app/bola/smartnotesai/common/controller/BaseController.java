package app.bola.smartnotesai.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

public interface BaseController<REQ, RES> {
	
	@PostMapping("new")
	ResponseEntity<RES> create(REQ req);
}
