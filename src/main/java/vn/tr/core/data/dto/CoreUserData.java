package vn.tr.core.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.tr.common.core.enums.LifecycleStatus;

import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
public class CoreUserData {
	
	private Long id;
	
	private String username;
	
	private String email;
	
	private String fullName;
	
	private String avatarUrl;
	
	private String userTypeCode;
	
	// Danh sách các thông tin liên hệ khác
	private List<CoreContactData> coreContactDatas;
	private Set<String> tagCodes;
	
	// Các thông tin quan hệ
	private Set<String> roles;
	private Set<String> groups;
	private Set<String> apps;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	private LifecycleStatus status;
	
	@JsonProperty("statusName")
	public String getStatusName() {
		return LifecycleStatus.getLabelByStatus(this.status);
	}
}
