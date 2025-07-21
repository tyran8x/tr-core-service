package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.jpa.entity.BaseCommonEntity;

@Entity
@Table(name = "core_group")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE core_group SET deleted_at = CURRENT_TIMESTAMP() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreGroup extends BaseCommonEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "parent_id")
	private Long parentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", insertable = false, updatable = false)
	@ToString.Exclude
	private CoreGroup parent;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
}
