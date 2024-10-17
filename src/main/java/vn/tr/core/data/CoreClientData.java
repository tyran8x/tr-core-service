package vn.tr.core.data;

import lombok.Data;

@Data
public class CoreClientData {

	private Long id;

	private String clientId;

	private String clientKey;

	private String clientSecret;

	private String grantType;

	private String deviceType;

	private Integer activeTimeout;

	private Integer timeout;

	private String appCode;

	private Boolean trangThai;

}
