package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_contact")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE core_contact SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreContact extends BaseEntity<Long> {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "owner_value", length = 100)
	private String ownerValue;
	
	@Column(name = "owner_type", length = 100)
	private String ownerType;
	
	@Column(name = "contact_type", length = 100)
	private String contactType;
	
	@Column(name = "label", length = 250)
	private String label;
	
	@Column(name = "value", length = 250)
	private String value;
	
	@Column(name = "is_primary")
	private Boolean isPrimary;
	
	@Column(name = "is_verified")
	private Boolean isVerified;
	
	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LifecycleStatus status = LifecycleStatus.ACTIVE;
	
	@Column(name = "sort_order", columnDefinition = "INTEGER DEFAULT 0")
	@Builder.Default
	private Integer sortOrder = 0;
	
}
