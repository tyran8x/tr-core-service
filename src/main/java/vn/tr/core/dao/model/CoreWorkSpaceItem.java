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
@Table(name = "core_workspace_item")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE core_workspace_item SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreWorkSpaceItem extends BaseCommonEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "parent_id")
	private Long parentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", insertable = false, updatable = false)
	@ToString.Exclude
	private CoreWorkSpaceItem parent;
	
	@Column(name = "owner_value", length = 100)
	private String ownerValue;
	
	@Column(name = "owner_type", length = 50)
	private String ownerType;
	
	@Column(name = "icon", length = 50)
	private String icon;
	
	@Column(name = "item_type", length = 50)
	private String itemType;
	
	@Column(name = "item_config", columnDefinition = "jsonb")
	private String itemConfig;
	
	@Column(name = "app_id")
	private String appCode;
	
}
