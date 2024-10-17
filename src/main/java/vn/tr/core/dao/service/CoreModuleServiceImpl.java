package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.tr.core.dao.model.CoreModule;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreModuleServiceImpl implements CoreModuleService {

	private final CoreModuleRepo repo;

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public boolean existsById(Long id) {
		return repo.existsById(id);
	}

	@Override
	public boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten) {
		return repo.existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(id, ten);
	}

	@Override
	public boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten) {
		return repo.existsByTenIgnoreCaseAndDaXoaFalse(ten);
	}

	@Override
	public Page<CoreModule> findAll(String search, Boolean trangThai, Pageable pageable) {
		return repo.findAll(CoreModuleSpecifications.quickSearch(search, trangThai), pageable);
	}

	@Override
	public List<CoreModule> findByDaXoaFalse() {
		return repo.findByDaXoaFalse();
	}

	@Override
	public Optional<CoreModule> findById(Long id) {
		return repo.findById(id);
	}

	@Override
	public Optional<CoreModule> findByIdAndDaXoaFalse(Long id) {
		return repo.findByIdAndDaXoaFalse(id);
	}

	@Override
	public List<CoreModule> findByIdInAndDaXoaFalse(List<Long> ids) {
		return repo.findByIdInAndDaXoaFalse(ids);
	}

	@Override
	public List<CoreModule> findByTrangThaiTrueAndDaXoaFalse() {
		return repo.findByTrangThaiTrueAndDaXoaFalse();
	}

	@Override
	public List<CoreModule> findByIdInAndTrangThaiTrueAndDaXoaFalse(List<Long> ids) {
		return repo.findByIdInAndTrangThaiTrueAndDaXoaFalse(ids);
	}

	@Override
	public CoreModule save(CoreModule coreModule) {
		return repo.save(coreModule);
	}

	@Override
	public int setFixedDaXoaForIds(boolean daXoa, List<Long> ids) {
		return repo.setFixedDaXoaForIds(daXoa, ids);
	}

}
