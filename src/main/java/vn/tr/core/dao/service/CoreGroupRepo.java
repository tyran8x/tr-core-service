package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreGroup;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreGroupRepo extends JpaRepository<CoreGroup, Long>, JpaSpecificationExecutor<CoreGroup> {

	boolean existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(long id, String ma);

	boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten);

	boolean existsByMaIgnoreCaseAndDaXoaFalse(String ma);

	boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten);

	Optional<CoreGroup> findFirstByMaIgnoreCaseAndDaXoaFalse(String ma);

	List<CoreGroup> findByMaIgnoreCaseInAndDaXoaFalse(Set<String> mas);

	List<CoreGroup> findByTrangThaiTrueAndDaXoaFalse();

}
