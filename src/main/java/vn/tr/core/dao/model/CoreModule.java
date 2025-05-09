package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_module")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreModule extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "ten", length = 250, nullable = false)
	private String ten;
	
	@Column(name = "ma", length = 150, nullable = false)
	private String ma;
	
	@Column(name = "cha_id")
	private Long chaId;
	
	@Column(name = "filedinhkem_id")
	private Long fileDinhKemId;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "sapxep")
	private Integer sapXep;
	
	@Column(name = "trangthai", columnDefinition = "boolean default 'true'")
	private Boolean trangThai;
	
}
