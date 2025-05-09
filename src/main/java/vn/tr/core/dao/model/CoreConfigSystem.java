package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

@Entity
@Table(name = "core_config_system")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreConfigSystem extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "maungdung", length = 200)
	private String maUngDung;
	
	@Column(name = "code", length = 500)
	private String code;
	
	@Column(name = "loaigiatri")
	private Integer loaiGiaTri;
	
	@Column(name = "giatri", length = 500)
	private String giaTri;
	
	@Column(name = "ghichu", columnDefinition = "TEXT")
	private String ghiChu;
	
	@Column(name = "app_code", length = 50)
	private String appCode;
	
	@Column(name = "trangthai", columnDefinition = "boolean default 'true'")
	private Boolean trangThai;
	
}
