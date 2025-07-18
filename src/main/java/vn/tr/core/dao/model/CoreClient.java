package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_client")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE core_client SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreClient extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "client_id", length = 64)
	private String clientId;
	
	@Column(name = "client_key", length = 32)
	private String clientKey;
	
	@Column(name = "client_secret", length = 250)
	private String clientSecret;
	
	@Column(name = "grant_type", length = 250)
	private String grantType;
	
	@Column(name = "device_type", length = 32)
	private String deviceType;
	
	@Column(name = "active_timeout")
	@ColumnDefault(value = "'1800'")
	private Integer activeTimeout;
	
	@Column(name = "timeout")
	@ColumnDefault(value = "'604800'")
	private Integer timeout;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LifecycleStatus status = LifecycleStatus.ACTIVE;
	
	@Column(name = "sort_order", columnDefinition = "INTEGER DEFAULT 0")
	@Builder.Default
	private Integer sortOrder = 0;
	
}
