package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import vn.tr.common.jpa.entity.BaseEntity;

import java.util.Objects;

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

	@Column(name = "user_name", length = 250, nullable = false, unique = true)
	private String userName;

	@Column(name = "email", length = 250)
	private String email;

	@Column(name = "password", length = 250)
	private String password;

	@Column(name = "nick_name", length = 250)
	private String nickName;

	@Column(name = "phone_number", length = 250)
	private String phoneNumber;

	@Column(name = "app_code", length = 50)
	private String appCode;

	@Column(name = "user_type")
	private String userType;
	
	@Column(name = "is_enabled")
	private Boolean isEnabled;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
				o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() :
				this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		CoreUser coreUser = (CoreUser) o;
		return getId() != null && Objects.equals(getId(), coreUser.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
				getClass().hashCode();
	}

}
