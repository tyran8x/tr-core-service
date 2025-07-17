package vn.tr.core.data.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import vn.tr.common.core.enums.LifecycleStatus;

@Data
public class CoreUserChangeStatusData {
	
	@NotNull(message = "Trạng thái mới không được để trống.")
	private LifecycleStatus newStatus;
	
}
