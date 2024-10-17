package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreConfigSystem;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CoreConfigSystemServiceImpl implements CoreConfigSystemService {
	
	private final CoreConfigSystemRepo repo;

	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}

	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}

	@Override
	public Page<CoreConfigSystem> findAll(String maUngDung, String code, Boolean trangThai, Pageable pageable) {
		return repo.findAll(CoreConfigSystemSpecifications.quickSearch(maUngDung, code, trangThai), pageable);
	}

	@Override
	public List<CoreConfigSystem> findByDaXoaFalse() {
		return repo.findByDaXoaFalse();
	}

	@Override
	public Optional<CoreConfigSystem> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Optional<CoreConfigSystem> findByIdAndDaXoaFalse(Long id) {
		return repo.findByIdAndDaXoaFalse(id);
	}

	@Override
	public List<CoreConfigSystem> findByIdInAndDaXoaFalse(List<Long> ids) {
		return repo.findByIdInAndDaXoaFalse(ids);
	}

	@Override
	public Optional<CoreConfigSystem> findFirstByCodeAndDaXoaFalse(String code) {
		return repo.findFirstByCodeAndDaXoaFalse(code);
	}

	@Override
	public String getGiaTriByCode(String code) {
		return repo.getGiaTriByCode(code);
	}

	@Override
	public CoreConfigSystem save(CoreConfigSystem coreConfigSystem) {
		return repo.save(coreConfigSystem);
	}

	@Override
	public int setFixedDaXoaByCode(boolean daXoa, String code) {
		return repo.setFixedDaXoaByCode(daXoa, code);
	}

	@Override
	public int setFixedDaXoaForIds(boolean daXoa, List<Long> ids) {
		return repo.setFixedDaXoaForIds(daXoa, ids);
	}

}
