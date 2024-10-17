package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CoreRoleServiceImpl implements CoreRoleService {

	private final CoreRoleRepo repo;

	public CoreRoleServiceImpl(CoreRoleRepo repo) {
		this.repo = repo;
	}

	@Override
	public Optional<CoreRole> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public CoreRole save(CoreRole coreRole) {
		return repo.save(coreRole);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public Page<CoreRole> findAll(String search, Boolean trangThai, String appCode, Pageable pageable) {
		return repo.findAll(CoreRoleSpecifications.quickSearch(search, trangThai, appCode), pageable);
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
	public Optional<CoreRole> findFirstByMaIgnoreCaseAndDaXoaFalse(String ma) {
		return repo.findFirstByMaIgnoreCaseAndDaXoaFalse(ma);
	}

	@Override
	public List<CoreRole> findByMaIgnoreCaseInAndDaXoaFalse(Set<String> mas) {
		return repo.findByMaIgnoreCaseInAndDaXoaFalse(mas);
	}

	@Override
	public List<CoreRole> findByTrangThaiTrueAndDaXoaFalse() {
		return repo.findByTrangThaiTrueAndDaXoaFalse();
	}

	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}

}
