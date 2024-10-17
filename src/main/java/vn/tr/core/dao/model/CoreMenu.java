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
@Table(name = "core_menu")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreMenu extends BaseEntity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ten", length = 1000, nullable = false)
	private String ten;

	@Column(name = "ma", length = 250, nullable = false)
	private String ma;

	@Column(name = "cha_id")
	private Long chaId;

	@Column(name = "mota", columnDefinition = "TEXT")
	private String moTa;

	@Column(name = "path")
	private String path;

	@Column(name = "component")
	private String component;

	@Column(name = "redirect")
	private String redirect;

	@Column(name = "is_hidden")
	@ColumnDefault(value = "'false'")
	private Boolean isHidden;

	@Column(name = "icon")
	private String icon;

	@Column(name = "is_alwaysshow")
	@ColumnDefault(value = "'false'")
	private Boolean isAlwaysShow;

	@Column(name = "is_cache")
	@ColumnDefault(value = "'false'")
	private Boolean isCache;

	@Column(name = "is_affix")
	@ColumnDefault(value = "'false'")
	private Boolean isAffix;

	@Column(name = "is_breadcrumb")
	@ColumnDefault(value = "'false'")
	private Boolean isBreadcrumb;

	@Column(name = "link")
	private String link;

	@Column(name = "is_frame")
	@ColumnDefault(value = "'false'")
	private Boolean isFrame;

	@Column(name = "active_menu")
	private String activeMenu;

	@Column(name = "props", columnDefinition = "TEXT")
	private String props;

	@Column(name = "app_code", length = 50)
	private String appCode;

	@Column(name = "sapxep")
	private Integer sapXep;

	@Column(name = "is_reload")
	@ColumnDefault(value = "'true'")
	private Boolean isReload;

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
		CoreMenu coreMenu = (CoreMenu) o;
		return getId() != null && Objects.equals(getId(), coreMenu.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
				getClass().hashCode();
	}

}
