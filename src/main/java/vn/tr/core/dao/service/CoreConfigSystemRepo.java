package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreConfigSystem;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreConfigSystemRepo extends JpaRepository<CoreConfigSystem, Long>, JpaSpecificationExecutor<CoreConfigSystem> {

	List<CoreConfigSystem> findByDaXoaFalse();

	Optional<CoreConfigSystem> findByIdAndDaXoaFalse(Long id);

	List<CoreConfigSystem> findByIdInAndDaXoaFalse(List<Long> ids);

	Optional<CoreConfigSystem> findFirstByCodeAndDaXoaFalse(String code);

	@Query(
			value = "SELECT u.giatri FROM core_config_system u WHERE u.code = ?1 AND u.daxoa = false AND u.trangthai = TRUE LIMIT 1",
			nativeQuery = true
	)
	String getGiaTriByCode(String code);

	@Modifying(clearAutomatically = true)
	@Query("update CoreConfigSystem u set u.daXoa = ?1 where code = ?2")
	int setFixedDaXoaByCode(boolean daXoa, String code);

	@Modifying(clearAutomatically = true)
	@Query("update CoreConfigSystem u set u.daXoa = ?1 where u.id IN ?2")
	int setFixedDaXoaForIds(boolean daXoa, List<Long> ids);
}
