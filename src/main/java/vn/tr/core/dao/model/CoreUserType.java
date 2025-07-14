package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_user_type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreUserType extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", length = 250, nullable = false)
	private String name;
	
	@Column(name = "code", length = 50, nullable = false, unique = true)
	private String code;
	
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "icon", length = 50)
	private String icon;
	
	@Column(name = "status")
	private Boolean status;
	
}
