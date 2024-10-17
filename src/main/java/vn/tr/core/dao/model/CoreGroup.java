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
@Table(name = "core_group")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreGroup extends BaseEntity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cha_id")
	private Long chaId;

	@Column(name = "ten", length = 250, nullable = false)
	private String ten;

	@Column(name = "ma", length = 50, nullable = false, unique = true)
	private String ma;

	@Column(name = "mota", columnDefinition = "TEXT")
	private String moTa;

	@Column(name = "app_code", length = 50)
	private String appCode;

	@Column(name = "sapxep")
	private Integer sapXep;

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
		CoreGroup coreGroup = (CoreGroup) o;
		return getId() != null && Objects.equals(getId(), coreGroup.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
				getClass().hashCode();
	}
}
