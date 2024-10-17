package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreGroupService {

	Optional<CoreGroup> findById(Long id);

	CoreGroup save(CoreGroup coreGroup);

	void delete(Long id);

	boolean existsById(Long id);

	Page<CoreGroup> findAll(String search, Boolean trangThai, String appCode, Pageable pageable);

	boolean existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(long id, String ma);

	boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten);

	boolean existsByMaIgnoreCaseAndDaXoaFalse(String ma);

	boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten);

	Optional<CoreGroup> findFirstByMaIgnoreCaseAndDaXoaFalse(String ma);

	List<CoreGroup> findByMaIgnoreCaseInAndDaXoaFalse(Set<String> mas);

	List<CoreGroup> findByTrangThaiTrueAndDaXoaFalse();

}
