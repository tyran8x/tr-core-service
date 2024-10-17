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
@Table(name = "core_user_manual")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreUserManual extends BaseEntity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ten", length = 3000)
	private String ten;

	@Column(name = "maungdung", length = 500)
	private String maUngDung;

	@Column(name = "sapxep")
	private Integer sapXep;

	@Column(name = "filedinhkem_id")
	private Long fileDinhKemId;

	@Column(name = "app_code", length = 50)
	private String appCode;

	@Column(name = "trangthai", columnDefinition = "boolean default 'true'")
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
		CoreUserManual that = (CoreUserManual) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
				getClass().hashCode();
	}
}
