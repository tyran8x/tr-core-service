package vn.tr.core.adapter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import vn.tr.core.data.dto.MetaData;
import vn.tr.core.data.dto.RouteRecordRawData;
import vn.tr.core.data.dto.RouterData;
import vn.tr.core.data.dto.RouterMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter chịu trách nhiệm chuyển đổi cấu trúc dữ liệu router cũ (Vue 2)
 * sang cấu trúc dữ liệu chuẩn (RouteRecordRawData) để đồng bộ hóa.
 *
 * @author tyran8x
 * @version 2.0
 */
@Component
public class LegacyRouteAdapter {
	
	public List<RouteRecordRawData> transform(List<RouterData> legacyRoutes) {
		if (legacyRoutes == null || legacyRoutes.isEmpty()) {
			return new ArrayList<>();
		}
		return legacyRoutes.stream()
				.map(this::mapLegacyToStandard)
				.collect(Collectors.toList());
	}
	
	private RouteRecordRawData mapLegacyToStandard(RouterData legacyRoute) {
		RouteRecordRawData newRoute = new RouteRecordRawData();
		
		// --- 1. Map các trường cấp cao nhất ---
		newRoute.setPath(legacyRoute.getPath());
		newRoute.setName(legacyRoute.getName());
		newRoute.setRedirect(legacyRoute.getRedirect());
		newRoute.setProps(legacyRoute.getProps());
		
		// --- 2. Map trường `meta` (quan trọng nhất) ---
		RouterMetaData newMeta = new RouterMetaData();
		if (legacyRoute.getMeta() != null) {
			MetaData legacyMeta = legacyRoute.getMeta();
			newMeta.setTitle(legacyMeta.getTitle());
			newMeta.setIcon(legacyMeta.getIcon());
			newMeta.setNoCache(legacyMeta.getNoCache());
			newMeta.setAffixTab(legacyMeta.getAffix());
			newMeta.setHideInBreadcrumb(legacyMeta.getBreadcrumb());
			newMeta.setHideInMenu(legacyRoute.getHidden());
			newMeta.setActiveMenu(legacyMeta.getActiveMenu());
			newMeta.setLink(legacyMeta.getLink());
		}
		newRoute.setMeta(newMeta);
		
		// --- 3. Xử lý logic `component` với quy tắc ưu tiên ---
		String finalComponent = "Layout"; // Giá trị mặc định
		if (legacyRoute.getComponent() instanceof String && StringUtils.isNotBlank((String) legacyRoute.getComponent())) {
			// Ưu tiên 2: Lấy từ component cấp cao nhất nếu là String
			finalComponent = (String) legacyRoute.getComponent();
		}
		// Bỏ qua nếu component là Object
		newRoute.setComponent(finalComponent);
		
		// --- 4. Đệ quy cho children ---
		if (legacyRoute.getChildren() != null && !legacyRoute.getChildren().isEmpty()) {
			newRoute.setChildren(transform(legacyRoute.getChildren()));
		}
		
		return newRoute;
	}
}
