package vn.tr.core.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class CoreDsMenuData {

	private String email;

	private Set<String> roles;

	private List<CoreMenuData> coreMenuDatas = new ArrayList<>();

}
