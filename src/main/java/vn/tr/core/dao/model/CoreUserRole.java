package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;
import vn.tr.common.jpa.entity.SoftDeletable;

@Entity
@Table(name = "core_user_role")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE core_user_role SET deleted_at = CURRENT_TIMESTAMP() WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreUserRole extends BaseEntity implements Identifiable<Long>, SoftDeletable {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "role_code")
	private String roleCode;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
}
