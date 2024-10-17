package vn.tr.core.dao.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.tr.core.dao.model.CoreRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CoreRoleRepo extends JpaRepository<CoreRole, Long>, JpaSpecificationExecutor<CoreRole> {

	boolean existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(long id, String ma);

	boolean existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(long id, String ten);

	boolean existsByMaIgnoreCaseAndDaXoaFalse(String ma);

	boolean existsByTenIgnoreCaseAndDaXoaFalse(String ten);

	Optional<CoreRole> findFirstByMaIgnoreCaseAndDaXoaFalse(String ma);

	List<CoreRole> findByMaIgnoreCaseInAndDaXoaFalse(Set<String> mas);

	List<CoreRole> findByTrangThaiTrueAndDaXoaFalse();

}
