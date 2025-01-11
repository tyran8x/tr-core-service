package vn.tr.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.tr.common.core.utils.StringUtils;

@Getter
@AllArgsConstructor
public enum AppConnect {
	
	ZALO("zalo"),
	
	GMAIL("gmail"),
	
	SMS("sms");
	
	private final String appConnect;
	
	public static AppConnect getAppConnect(String str) {
		for (AppConnect value : values()) {
			if (StringUtils.contains(str, value.getAppConnect())) {
				return value;
			}
		}
		throw new RuntimeException("'AppConnect' not found By " + str);
	}
}
