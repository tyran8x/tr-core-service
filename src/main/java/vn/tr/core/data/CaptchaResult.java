package vn.tr.core.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaResult {

	private String key;

	private String base64;

	private Boolean isEnable;

}
