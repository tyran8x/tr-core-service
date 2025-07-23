package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;

@Entity
@Table(name = "core_user")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE core_user SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreUser extends BaseEntity implements Identifiable<Long> {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username", length = 250, nullable = false, unique = true)
	private String username;
	
	@Column(name = "email", length = 250, unique = true)
	private String email;
	
	@Column(name = "hashed_password", length = 250)
	private String hashedPassword;
	
	@Column(name = "full_name", length = 250)
	private String fullName;
	
	@Column(name = "avatar_url", columnDefinition = "TEXT")
	private String avatarUrl;
	
	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LifecycleStatus status = LifecycleStatus.ACTIVE;
	
	@Column(name = "sort_order", columnDefinition = "INTEGER DEFAULT 0")
	@Builder.Default
	private Integer sortOrder = 0;
	
}
