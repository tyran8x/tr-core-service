package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.proxy.HibernateProxy;
import vn.tr.common.jpa.entity.BaseEntity;

import java.util.Objects;

@Entity
@Table(name = "core_client")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreClient extends BaseEntity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "client_id", length = 64)
	private String clientId;

	@Column(name = "client_key", length = 32)
	private String clientKey;

	@Column(name = "client_secret", length = 250)
	private String clientSecret;

	@Column(name = "grant_type", length = 250)
	private String grantType;

	@Column(name = "device_type", length = 32)
	private String deviceType;

	@Column(name = "active_timeout")
	@ColumnDefault(value = "'1800'")
	private Integer activeTimeout;

	@Column(name = "timeout")
	@ColumnDefault(value = "'604800'")
	private Integer timeout;

	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "trangthai")
	@ColumnDefault(value = "'true'")
	private Boolean trangThai;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
				o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() :
				this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		CoreClient that = (CoreClient) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
				getClass().hashCode();
	}

}
