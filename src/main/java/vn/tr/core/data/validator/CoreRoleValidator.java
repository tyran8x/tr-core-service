package vn.tr.core.data.validator;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.tr.core.dao.service.CoreRoleService;
import vn.tr.core.data.CoreRoleData;

import java.util.Objects;

@Component
public class CoreRoleValidator implements Validator {

	protected final MessageSource messageSource;
	protected final CoreRoleService coreRoleService;

	public CoreRoleValidator(MessageSource messageSource, CoreRoleService coreRoleService) {
		this.messageSource = messageSource;
		this.coreRoleService = coreRoleService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return CoreRoleData.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CoreRoleData object = (CoreRoleData) target;

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
				return coreRoleService.existsByIdNotAndMaIgnoreCaseAndDaXoaFalse(id, ma);
			}
			return coreRoleService.existsByMaIgnoreCaseAndDaXoaFalse(ma);
		}
		return false;
	}

	private boolean checkTenExits(Long id, String ten) {
		if (StrUtil.isNotBlank(ten)) {
			if (Objects.nonNull(id)) {
				return coreRoleService.existsByIdNotAndTenIgnoreCaseAndDaXoaFalse(id, ten);
			}
			return coreRoleService.existsByTenIgnoreCaseAndDaXoaFalse(ten);
		}
		return false;
	}
}
