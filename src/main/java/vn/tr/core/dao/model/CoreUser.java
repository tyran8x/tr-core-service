package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_user")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreUser extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "username", length = 250, nullable = false, unique = true)
	private String username;
	
	@Column(name = "hashed_password", length = 250)
	private String hashedPassword;
	
	@Column(name = "full_name", length = 250)
	private String fullName;
	
	@Column(name = "user_type_id")
	private Long userTypeId;
	
	@Column(name = "status", nullable = false)
	@ColumnDefault("ACTIVE")
	private String status = "ACTIVE";
	
}
