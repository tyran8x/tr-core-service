package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
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
	
	@Column(name = "owner_id")
	private Long ownerId;
	
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
	
	@Column(name = "status", nullable = false)
	@ColumnDefault("ACTIVE")
	private String status = "ACTIVE";
	
	@Column(name = "sort_order")
	@ColumnDefault("0")
	private Integer sortOrder;
	
}
