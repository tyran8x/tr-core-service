package vn.tr.core.dao.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.tr.common.core.domain.dto.DictDataDTO;
import vn.tr.common.core.domain.dto.DictTypeDTO;
import vn.tr.common.core.service.DictService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CoreDictTypeSeviceImpl implements DictService {
	@Override
	public String getDictLabel(String dictType, String dictValue, String separator) {
		return "";
	}
	
	@Override
	public String getDictValue(String dictType, String dictLabel, String separator) {
		return "";
	}
	
	@Override
	public Map<String, String> getAllDictByDictType(String dictType) {
		return Map.of();
	}
	
	@Override
	public DictTypeDTO getDictType(String dictType) {
		return null;
	}
	
	@Override
	public List<DictDataDTO> getDictData(String dictType) {
		return List.of();
	}
}
