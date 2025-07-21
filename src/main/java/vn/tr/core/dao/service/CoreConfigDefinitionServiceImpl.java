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
	
	private final CoreConfigDefinitionRepo coreConfigDefinitionRepo;
	
	@Override
	public void deleteById(Long id) {
		coreConfigDefinitionRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreConfigDefinitionRepo.existsById(id);
	}
	
	@Override
	public Page<CoreConfigDefinition> findAll(String maUngDung, String code, Boolean trangThai, Pageable pageable) {
		return coreConfigDefinitionRepo.findAll(CoreConfigDefinitionSpecifications.quickSearch(maUngDung, code, trangThai), pageable);
	}
	
	@Override
	public Optional<CoreConfigDefinition> findById(Long id) {
		return coreConfigDefinitionRepo.findById(id);
	}
	
}
