package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreModule;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreModuleRepo extends JpaRepository<CoreModule, Long>, JpaSpecificationExecutor<CoreModule> {

	boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten);

	boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten);

	List<CoreModule> findByDaXoaFalse();

	Optional<CoreModule> findByIdAndDaXoaFalse(Long id);

	List<CoreModule> findByIdInAndDaXoaFalse(List<Long> ids);

	List<CoreModule> findByTrangThaiTrueAndDaXoaFalse();

	List<CoreModule> findByIdInAndTrangThaiTrueAndDaXoaFalse(List<Long> ids);

	@Modifying(clearAutomatically = true)
	@Query("update CoreModule u set u.daXoa = ?1 where u.id IN ?2")
	int setFixedDaXoaForIds(boolean daXoa, List<Long> ids);
}
