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
@Table(name = "core_attachment")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreAttachment extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "code", length = 250)
	private String code;
	@Column(name = "folder", length = 250, nullable = false)
	private String folder;
	@Column(name = "app_code", length = 250, nullable = false)
	private String appCode;
	@Column(name = "base64", columnDefinition = "TEXT")
	private String base64;
	@Column(name = "month")
	private Integer month;
	@Column(name = "year")
	private Integer year;
	@Column(name = "link")
	private String link;
	@Column(name = "size")
	private Long size;
	@Column(name = "mime", length = 250, nullable = false)
	private String mime;
	@Column(name = "file_name", length = 250, nullable = false)
	private String fileName;
	@Column(name = "object_id")
	private Long objectId;
	@Column(name = "type")
	private Integer type;
	
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
				o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() :
				this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		CoreAttachment that = (CoreAttachment) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}
	
	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
				getClass().hashCode();
	}
	
}
