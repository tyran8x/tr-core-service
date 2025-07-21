package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;
import vn.tr.common.jpa.entity.SoftDeletable;

@Entity
@Table(name = "core_config_value")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE core_config_value SET deleted_at = CURRENT_TIMESTAMP() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreConfigValue extends BaseEntity implements Identifiable<Long>, SoftDeletable {
	
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
