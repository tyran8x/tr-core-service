package vn.tr.core.data;

import lombok.Data;

@Data
public class CoreAttachmentData {

	private Long id;

	private String code;

	private String folder;

	private String appCode;

	private String base64;

	private Integer month;

	private Integer year;

	private String link;

	private Long size;

	private String mime;

	private String fileName;

	private Long objectId;

	private Integer type;

}
