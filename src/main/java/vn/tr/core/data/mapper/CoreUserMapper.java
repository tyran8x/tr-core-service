package vn.tr.core.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.data.dto.CoreUserData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoreUserMapper {
	
	@Mapping(target = "password", ignore = true) // Không bao giờ map mật khẩu ra DTO
	@Mapping(target = "roles", ignore = true) // Sẽ được xử lý riêng
	@Mapping(target = "groups", ignore = true)
	@Mapping(target = "apps", ignore = true)
	CoreUserData toData(CoreUser entity);
	
	// Chuyển từ DTO sang Entity để tạo mới
	@Mapping(target = "id", ignore = true) // Bỏ qua id khi tạo mới
	@Mapping(target = "hashedPassword", ignore = true) // Mật khẩu sẽ được xử lý riêng
	@Mapping(target = "status", constant = "ACTIVE")
	// Giá trị mặc định
	CoreUser toEntity(CoreUserData data);
	
	// Cập nhật Entity từ DTO
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "username", ignore = true) // Không cho phép đổi username
	@Mapping(target = "hashedPassword", ignore = true)
	void updateEntity(CoreUserData data, @MappingTarget CoreUser entity);
	
}
