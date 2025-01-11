package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_user_connect")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreUserConnect extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_name", length = 250)
	private String userName;
	
	@Column(name = "app_user_id", length = 250)
	private String appUserId;
	
	@Column(name = "app_user_id_by_app", length = 250)
	private String appUserIdByApp;
	
	@Column(name = "app_avatar", length = 500)
	private String appAvatar;
	
	@Column(name = "app_display_name", length = 500)
	private String appDisplayName;
	
	@Column(name = "app_name", length = 250)
	private String appName;
	
}
