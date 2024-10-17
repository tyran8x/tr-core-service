package vn.tr.core.data.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.core.dao.service.CoreModuleService;
import vn.tr.core.data.CoreModuleData;

import java.util.Objects;

@Component
public class CoreModuleValidator implements Validator {
	@Autowired
	protected MessageSource messageSource;
	@Autowired
	protected CoreModuleService coreModuleService;

	private boolean checkTenExits(Long id, String ten) {
		if (StringUtils.isNotEmpty(ten)) {
			if (Objects.nonNull(id)) {
				return coreModuleService.existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(id, ten);
			}
			return coreModuleService.existsByTenIgnoreCaseAndDaXoaFalse(ten);
		}
		return false;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return CoreModuleData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CoreModuleData object = (CoreModuleData) target;

		if (checkTenExits(object.getId(), object.getTen())) {
			errors.rejectValue("ten", "error.ten", new Object[]{"ten"}, "Tên đã tồn tại");
		}
	}
}
