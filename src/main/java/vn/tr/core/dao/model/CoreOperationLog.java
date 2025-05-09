package vn.tr.core.dao.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import vn.tr.common.jpa.entity.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "core_operation_log")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CoreOperationLog extends BaseEntity {
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "title", columnDefinition = "text")
	private String title;
	
	/**
	 * Business type (0 other 1 new 2 modified 3 deleted)
	 */
	@Column(name = "business_type")
	private Integer businessType;
	
	@Column(name = "method", columnDefinition = "text")
	private String method;
	
	@Column(name = "request_method", columnDefinition = "text")
	private String requestMethod;
	
	@Column(name = "type")
	private Integer type;
	
	@Column(name = "name", columnDefinition = "text")
	private String name;
	
	@Column(name = "url", columnDefinition = "text")
	private String url;
	
	@Column(name = "ip", columnDefinition = "text")
	private String ip;
	
	@Column(name = "location", columnDefinition = "text")
	private String location;
	
	@Column(name = "params", columnDefinition = "text")
	private String params;
	
	@Column(name = "json_result", columnDefinition = "text")
	private String jsonResult;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "error_message", columnDefinition = "text")
	private String errorMessage;
	
	@Column(name = "time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDate time;
	
	@Column(name = "cost_time")
	private Long costTime;
	
}
