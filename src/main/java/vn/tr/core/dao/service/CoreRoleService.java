package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CoreRoleService {

	Optional<CoreRole> findById(Long id);

	CoreRole save(CoreRole coreRole);

	void delete(Long id);

	boolean existsById(Long id);

	Page<CoreRole> findAll(String search, Boolean trangThai, String appCode, Pageable pageable);

	boolean existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(long id, String ma);

	boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten);

	boolean existsByMaIgnoreCaseAndDaXoaFalse(String ma);

	boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten);

	Optional<CoreRole> findFirstByMaIgnoreCaseAndDaXoaFalse(String ma);

	List<CoreRole> findByMaIgnoreCaseInAndDaXoaFalse(Set<String> mas);

	List<CoreRole> findByTrangThaiTrueAndDaXoaFalse();

}
