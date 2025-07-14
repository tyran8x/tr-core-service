package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseCatalogEntity;

@Entity
@Table(name = "core_group")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreGroup extends BaseCatalogEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "parent_id")
	private Long parentId;
	
	@Column(name = "app_id")
	private Long appId;
	
}
