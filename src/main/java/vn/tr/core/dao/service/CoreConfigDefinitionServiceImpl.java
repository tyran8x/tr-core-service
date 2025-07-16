package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreConfigDefinition;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CoreConfigDefinitionServiceImpl implements CoreConfigDefinitionService {
	
	private final CoreConfigDefinitionRepo repo;
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}
	
	@Override
	public Page<CoreConfigDefinition> findAll(String maUngDung, String code, Boolean trangThai, Pageable pageable) {
		return repo.findAll(CoreConfigDefinitionSpecifications.quickSearch(maUngDung, code, trangThai), pageable);
	}
	
	@Override
	public Optional<CoreConfigDefinition> findById(Long id) {
		return repo.findById(id);
	}
	
}
