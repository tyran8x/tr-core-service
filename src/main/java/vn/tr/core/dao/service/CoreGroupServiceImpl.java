package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreGroupServiceImpl implements CoreGroupService {

	private final CoreGroupRepo repo;

	public CoreGroupServiceImpl(CoreGroupRepo repo) {
		this.repo = repo;
	}

	@Override
	public Optional<CoreGroup> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public CoreGroup save(CoreGroup coreGroup) {
		return repo.save(coreGroup);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public Page<CoreGroup> findAll(String search, Boolean trangThai, String appCode, Pageable pageable) {
		return repo.findAll(CoreGroupSpecifications.quickSearch(search, trangThai, appCode), pageable);
	}

	@Override
	public boolean existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(long id, String ma) {
		return repo.existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(id, ma);
	}

	@Override
	public boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten) {
		return repo.existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(id, ten);
	}

	@Override
	public boolean existsByMaIgnoreCaseAndDaXoaFalse(String ma) {
		return repo.existsByMaIgnoreCaseAndDaXoaFalse(ma);
	}

	@Override
	public boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten) {
		return repo.existsByTenIgnoreCaseAndDaXoaFalse(ten);
	}

	@Override
	public Optional<CoreGroup> findFirstByMaIgnoreCaseAndDaXoaFalse(String ma) {
		return repo.findFirstByMaIgnoreCaseAndDaXoaFalse(ma);
	}

	@Override
	public List<CoreGroup> findByMaIgnoreCaseInAndDaXoaFalse(Set<String> mas) {
		return repo.findByMaIgnoreCaseInAndDaXoaFalse(mas);
	}

	@Override
	public List<CoreGroup> findByTrangThaiTrueAndDaXoaFalse() {
		return repo.findByTrangThaiTrueAndDaXoaFalse();
	}

	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}

}
