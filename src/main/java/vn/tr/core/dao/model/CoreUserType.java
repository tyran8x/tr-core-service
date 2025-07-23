package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.jpa.entity.BaseCommonEntity;

@Entity
@Table(name = "core_user_type")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE core_user_type SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreUserType extends BaseCommonEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
}
