package vn.tr.core.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CoreJournalData {

	private Long id;

	private Long objectId;

	private String objectType;

	private String appCode;

	private String note;

	private Boolean isPrivate;

	private String nguoiTao;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime ngayTao;

	private List<CoreJournalDetailData> coreJournalDetailDatas = new ArrayList<>();

}
