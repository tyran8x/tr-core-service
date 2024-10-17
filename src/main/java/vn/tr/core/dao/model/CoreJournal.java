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
@Table(name = "core_journal")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreJournal extends BaseEntity {

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "object_id")
	private Long objectId;

	@Column(name = "object_type", length = 250)
	private String objectType;

	@Column(name = "app_code", length = 50)
	private String appCode;

	@Column(name = "note", length = 2000)
	private String note;

	@Column(name = "is_private", columnDefinition = "boolean default 'false'")
	private Boolean isPrivate;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
				o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() :
				this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		CoreJournal that = (CoreJournal) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
				getClass().hashCode();
	}
}
