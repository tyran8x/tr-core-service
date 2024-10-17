package vn.tr.core.data.validator;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.core.dao.service.CoreGroupService;
import vn.tr.core.data.CoreGroupData;

import java.util.Objects;

@Component
public class CoreGroupValidator implements Validator {

	protected final MessageSource messageSource;
	protected final CoreGroupService coreGroupService;

	public CoreGroupValidator(MessageSource messageSource, CoreGroupService coreGroupService) {
		this.messageSource = messageSource;
		this.coreGroupService = coreGroupService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return CoreGroupData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CoreGroupData object = (CoreGroupData) target;

		if (checkMaExits(object.getId(), object.getMa())) {
			errors.rejectValue("ma", "error.ma", new Object[]{"ma"}, "Mã đã tồn tại");
		}
		if (checkTenExits(object.getId(), object.getTen())) {
			errors.rejectValue("ten", "error.ten", new Object[]{"ten"}, "Tên đã tồn tại");
		}
	}

	private boolean checkMaExits(Long id, String ma) {
		if (StrUtil.isNotBlank(ma)) {
			if (Objects.nonNull(id)) {
				return coreGroupService.existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(id, ma);
			}
			return coreGroupService.existsByMaIgnoreCaseAndDaXoaFalse(ma);
		}
		return false;
	}

	private boolean checkTenExits(Long id, String ten) {
		if (StrUtil.isNotBlank(ten)) {
			if (Objects.nonNull(id)) {
				return coreGroupService.existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(id, ten);
			}
			return coreGroupService.existsByTenIgnoreCaseAndDaXoaFalse(ten);
		}
		return false;
	}
}
