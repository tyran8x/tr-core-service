package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreOperationLog;

import java.util.Optional;

public interface CoreOperationLogService {

	Optional<CoreOperationLog> findById(Long id);

	CoreOperationLog save(CoreOperationLog coreActivityLog);

	void delete(Long id);

	boolean existsById(Long id);

	Page<CoreOperationLog> findAll(String search, Boolean trangThai, String appCode, Pageable pageable);

}
