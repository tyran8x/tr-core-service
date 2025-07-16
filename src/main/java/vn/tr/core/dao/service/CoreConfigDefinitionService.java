package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreConfigDefinition;

import java.util.Optional;

public interface CoreConfigDefinitionService {
	
	void deleteById(Long id);
	
	boolean existsById(Long id);
	
	Page<CoreConfigDefinition> findAll(String maUngDung, String code, Boolean trangThai, Pageable pageable);
	
	Optional<CoreConfigDefinition> findById(Long id);
	
}
