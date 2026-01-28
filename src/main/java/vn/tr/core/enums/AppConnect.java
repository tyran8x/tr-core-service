package vn.tr.core.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppConnect {
	
	ZALO("zalo"),
	
	GMAIL("gmail"),
	
	SMS("sms");
	
	private final String appConnect;
	
	public static AppConnect getAppConnect(String str) {
		for (AppConnect value : values()) {
			if (StrUtil.contains(str, value.getAppConnect())) {
				return value;
			}
		}
		throw new RuntimeException("'AppConnect' not found By " + str);
	}
}
