package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vn.tr.common.core.utils.ip.AddressUtils;
import vn.tr.common.log.event.OperationLogEvent;
import vn.tr.core.dao.model.CoreOperationLog;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoreOperationLogServiceImpl implements CoreOperationLogService {
	
	private final CoreOperationLogRepo coreOperationLogRepo;
	
	@Async("scheduledExecutorService")
	@EventListener
	public void recordOperationLog(OperationLogEvent operationLogEvent) {
		CoreOperationLog coreOperationLog = new CoreOperationLog();
		coreOperationLog.setDaXoa(false);
		coreOperationLog.setId(operationLogEvent.getId());
		coreOperationLog.setTitle(operationLogEvent.getTitle());
		coreOperationLog.setIp(operationLogEvent.getIp());
		coreOperationLog.setBusinessType(operationLogEvent.getBusinessType());
		coreOperationLog.setMethod(operationLogEvent.getMethod());
		coreOperationLog.setCostTime(operationLogEvent.getCostTime());
		coreOperationLog.setName(operationLogEvent.getName());
		coreOperationLog.setStatus(operationLogEvent.getStatus());
		coreOperationLog.setJsonResult(operationLogEvent.getJsonResult());
		coreOperationLog.setUrl(operationLogEvent.getUrl());
		coreOperationLog.setType(operationLogEvent.getType());
		coreOperationLog.setTime(operationLogEvent.getTime());
		coreOperationLog.setParams(operationLogEvent.getParam());
		coreOperationLog.setRequestMethod(operationLogEvent.getRequestMethod());
		coreOperationLog.setErrorMessage(operationLogEvent.getErrorMessage());
		coreOperationLog.setLocation(AddressUtils.getRealAddressByIP(operationLogEvent.getIp()));
		save(coreOperationLog);
	}
	
	@Override
	public Optional<CoreOperationLog> findById(Long id) {
		return coreOperationLogRepo.findById(id);
	}
	
	@Override
	public CoreOperationLog save(CoreOperationLog coreOperationLog) {
		return coreOperationLogRepo.save(coreOperationLog);
	}
	
	@Override
	public void delete(Long id) {
		coreOperationLogRepo.deleteById(id);
	}
	
	@Override
	public boolean existsById(Long id) {
		return coreOperationLogRepo.existsById(id);
	}
	
	@Override
	public Page<CoreOperationLog> findAll(String search, Boolean trangThai, String appCode, Pageable pageable) {
		return coreOperationLogRepo.findAll(CoreOperationLogSpecifications.quickSearch(search, trangThai, appCode), pageable);
	}
	
}
