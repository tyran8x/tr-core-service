package vn.tr.core.dao.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.tr.core.dao.model.CoreUserConnect;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CoreUserConnectServiceImpl implements CoreUserConnectService {
	
	private final CoreUserConnectRepo repo;
	
	public CoreUserConnectServiceImpl(CoreUserConnectRepo repo) {
		this.repo = repo;
	}
	
	@Override
	public CoreUserConnect save(CoreUserConnect dmTaiLieuHdsd) {
		return repo.save(dmTaiLieuHdsd);
	}
	
	@Override
	public void deleteById(Long id) {
		repo.deleteById(id);
	}
	
	@Override
	public Optional<CoreUserConnect> findById(Long id) {
		return repo.findById(id);
	}
	
	@Override
	public Page<CoreUserConnect> findAll(String search, Boolean trangThai, List<String> maUngDungs, String appCode, Pageable pageable) {
		return repo.findAll(CoreUserConnectSpecifications.quickSearch(search, trangThai, maUngDungs, appCode), pageable);
	}
	
	@Override
	public Optional<CoreUserConnect> findByUserNameIgnoreCaseAndAppNameAndDaXoaFalse(String userName, String appName) {
		return repo.findByUserNameIgnoreCaseAndAppNameAndDaXoaFalse(userName, appName);
	}
	
	@Override
	public List<CoreUserConnect> findByUserNameIgnoreCaseAndDaXoaFalse(String userName) {
		return repo.findByUserNameIgnoreCaseAndDaXoaFalse(userName);
	}
}
