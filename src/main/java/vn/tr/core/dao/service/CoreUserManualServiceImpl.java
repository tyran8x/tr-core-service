package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserManual;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreUserManualServiceImpl implements CoreUserManualService {

	private final CoreUserManualRepo repo;

	public CoreUserManualServiceImpl(CoreUserManualRepo repo) {
		this.repo = repo;
	}

	@Override
	public CoreUserManual save(CoreUserManual dmTaiLieuHdsd) {
		return repo.save(dmTaiLieuHdsd);
	}

	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}

	@Override
	public Optional<CoreUserManual> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Page<CoreUserManual> findAll(String search, Boolean trangThai, List<String> maUngDungs, String appCode, Pageable pageable) {
		return repo.findAll(CoreUserManualSpecifications.quickSearch(search, trangThai, maUngDungs, appCode), pageable);
	}
}
