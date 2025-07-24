package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;

@Entity
@Table(name = "core_config_definition")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE core_config_definition SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreConfigDefinition extends BaseEntity implements Identifiable<Long> {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "key", length = 150, nullable = false)
	private String key;
	
	@Column(name = "name", length = 500, nullable = false)
	private String name;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "data_type", nullable = false, length = 50)
	//  ENUM: 'STRING', 'TEXT', 'INTEGER', 'NUMBER', 'BOOLEAN', 'JSON', 'SECRET'
	@Builder.Default
	private String dataType = "STRING";
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "validation_rules", columnDefinition = "JSONB")
	private String validationRules;
	
	@Column(name = "default_value", columnDefinition = "TEXT")
	private String defaultValue;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "is_public", columnDefinition = "boolean default 'false'")
	private Boolean isPublic;
	
	@Column(name = "is_encrypted", columnDefinition = "boolean default 'false'")
	private Boolean isEncrypted;
	
	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LifecycleStatus status = LifecycleStatus.ACTIVE;
	
	@Column(name = "sort_order", columnDefinition = "INTEGER DEFAULT 0")
	@Builder.Default
	private Integer sortOrder = 0;
	
}
