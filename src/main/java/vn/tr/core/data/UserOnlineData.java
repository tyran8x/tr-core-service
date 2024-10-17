package vn.tr.core.data;

import lombok.Data;

@Data
public class UserOnlineData {

	private String tokenId;

	private String deptName;

	private String userName;

	private String clientKey;

	private String deviceType;

	private String ipaddr;

	private String loginLocation;

	private String browser;

	private String os;

	private Long loginTime;

}
