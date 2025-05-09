package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_role")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreRole extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "ten", length = 250, nullable = false)
	private String ten;
	
	@Column(name = "ma", length = 50, nullable = false, unique = true)
	private String ma;
	
	@Column(name = "mota", columnDefinition = "TEXT")
	private String moTa;
	
	@Column(name = "is_default")
	private Boolean isDefault;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "sapxep")
	private Integer sapXep;
	
	@Column(name = "trangthai")
	@ColumnDefault(value = "'true'")
	private Boolean trangThai;
	
}
