package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_role2menu")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreRole2Menu extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "menu_id")
	private Long menuId;
	
	@Column(name = "role_id")
	private Long roleId;
	
}
