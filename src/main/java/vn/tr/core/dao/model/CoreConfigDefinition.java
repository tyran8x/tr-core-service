package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_config_definition")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreConfigDefinition extends BaseEntity {
	
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
	private String dataType = "STRING";
	
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
	
	@Column(name = "status", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
	private String status = "ACTIVE";
	
}
