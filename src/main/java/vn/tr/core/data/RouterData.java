package vn.tr.core.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RouterData {

	private String path;

	private Object component;

	private String redirect;

	private String name;

	private MetaData meta;

	private List<RouterData> children = new ArrayList<>();

	private Boolean hidden;

	private Boolean alwaysShow;

	private Object props;

}
