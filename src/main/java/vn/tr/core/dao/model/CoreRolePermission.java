package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;

@Entity
@Table(name = "core_role_permission")
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE core_role_permission SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreRolePermission extends BaseEntity implements Identifiable<Long> {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "role_code", length = 250)
	private String roleCode;
	
	@Column(name = "permission_code", length = 250)
	private String permissionCode;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
}
