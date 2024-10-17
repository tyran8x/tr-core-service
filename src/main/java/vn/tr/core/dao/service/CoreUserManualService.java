package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tr.core.dao.model.CoreUserManual;

import java.util.List;
import java.util.Optional;

public interface CoreUserManualService {

	CoreUserManual save(CoreUserManual coreUserManual);

	void deleteById(Long id);

	Optional<CoreUserManual> findById(Long id);

	Page<CoreUserManual> findAll(String search, Boolean trangThai, List<String> maUngDungs, String appCode, Pageable pageable);
}
