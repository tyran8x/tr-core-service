package vn.tr.core.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CoreJournalDetailData {

	private Long id;

	private Long journalId;

	private Long parentId;

	private String property;

	private String propKey;

	private String oldValue;

	private String newValue;

	private List<CoreJournalDetailData> children = new ArrayList<>();

}
