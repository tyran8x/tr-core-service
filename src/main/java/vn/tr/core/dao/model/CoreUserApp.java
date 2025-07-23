package vn.tr.core.dao.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import vn.tr.common.core.enums.LifecycleStatus;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_user_app")
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE core_user_app SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction(value = "deleted_at IS NULL")
public class CoreUserApp extends BaseEntity implements Identifiable<Long> {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LifecycleStatus status = LifecycleStatus.ACTIVE;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "user_type_code", length = 50)
	private String userTypeCode;
	
	@Column(name = "assigned_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	@Builder.Default
	private LocalDateTime assignedAt = LocalDateTime.now();
	
}
