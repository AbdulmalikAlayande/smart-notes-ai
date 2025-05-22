package app.bola.smartnotesai.common.service;

import java.util.Collection;
import java.util.Collections;

public interface BaseService<REQ, ENT, RES> {

    RES create(REQ req);
	
    RES findByPublicId(String publicId);
	
    default RES toResponse(ENT ent) {
        return null;
    }
	
	default Collection<RES> toResponse(Collection<ENT> entities) {
		return Collections.emptyList();
	}
	
	RES update(String publicId, Object req);
}
