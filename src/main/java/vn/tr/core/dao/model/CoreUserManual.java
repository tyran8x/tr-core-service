package vn.tr.core.dao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;
import vn.tr.common.jpa.entity.Identifiable;

@Entity
@Table(name = "core_user_manual")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreUserManual extends BaseEntity implements Identifiable<Long> {
	
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
	
}
