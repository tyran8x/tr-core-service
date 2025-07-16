package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_contact")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreContact extends BaseEntity {
	
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
	
	@Column(name = "status", nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'ACTIVE'")
	private String status = "ACTIVE";
	
	@Column(name = "sort_order", columnDefinition = "INTEGER DEFAULT 0")
	private Integer sortOrder = 0;
	
}
