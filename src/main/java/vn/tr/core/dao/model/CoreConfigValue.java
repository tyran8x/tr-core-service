package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_config_value")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreConfigValue extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "definition_id")
	private Long definitionId;
	
	@Column(name = "scope_value", length = 100)
	private String scopeValue;
	
	@Column(name = "scope_type", length = 100)
	private String scopeType;
	
	@Column(name = "value", columnDefinition = "TEXT")
	private String value;
	
}
