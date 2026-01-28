package vn.tr.core.adapter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.tr.core.data.dto.MetaData;
import vn.tr.core.data.dto.RouteRecordRawData;
import vn.tr.core.data.dto.RouterData;
import vn.tr.core.data.dto.RouterMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adapter chịu trách nhiệm chuyển đổi cấu trúc dữ liệu router cũ (Vue 2) sang cấu trúc dữ liệu chuẩn (RouteRecordRawData) để đồng bộ hóa.
 *
 * @author tyran8x
 * @version 2.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LegacyRouteAdapter {
	
	private final ObjectMapper objectMapper;
	
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
		
		newRoute.setPath(legacyRoute.getPath());
		newRoute.setName(legacyRoute.getName());
		newRoute.setRedirect(legacyRoute.getRedirect());
		newRoute.setProps(legacyRoute.getProps());
		
		RouterMetaData newMeta = new RouterMetaData();
		if (legacyRoute.getMeta() != null) {
			MetaData legacyMeta = legacyRoute.getMeta();
			newMeta.setTitle(legacyMeta.getTitle());
			newMeta.setIcon(legacyMeta.getIcon());
			newMeta.setNoCache(Boolean.TRUE.equals(legacyMeta.getNoCache()));
			newMeta.setAffixTab(Boolean.TRUE.equals(legacyMeta.getAffix()));
			newMeta.setHideInBreadcrumb(Boolean.TRUE.equals(legacyMeta.getBreadcrumb()));
			newMeta.setHideInMenu(Boolean.TRUE.equals(legacyRoute.getHidden()));
			newMeta.setActiveMenu(legacyMeta.getActiveMenu());
			newMeta.setLink(legacyMeta.getLink());
		}
		newRoute.setMeta(newMeta);
		newRoute.setComponent(determineComponentValue(legacyRoute));
		
		// --- 4. Đệ quy cho children ---
		if (legacyRoute.getChildren() != null && !legacyRoute.getChildren().isEmpty()) {
			newRoute.setChildren(transform(legacyRoute.getChildren()));
		}
		
		return newRoute;
	}
	
	/**
	 * Helper chứa logic nghiệp vụ để xác định giá trị cuối cùng cho trường 'component', dựa trên cấu trúc thực tế của Vue 2 router.
	 *
	 * @param legacyRoute Route ở định dạng cũ.
	 *
	 * @return Giá trị String của component, hoặc null nếu không xác định được.
	 */
	private String determineComponentValue(RouterData legacyRoute) {
		// Log dữ liệu đầu vào để debug
		log.info("Determining component for route: name='{}'", legacyRoute.getName());
		
		MetaData legacyMeta = legacyRoute.getMeta();
		
		// **QUY TẮC 1: ƯU TIÊN CAO NHẤT - Lấy component từ META**
		// Hầu hết các trang chức năng (route lá) sẽ rơi vào trường hợp này.
		if (legacyMeta != null && StrUtil.isNotBlank(legacyMeta.getComponent())) {
			return legacyMeta.getComponent();
		}
		
		// **QUY TẮC 2: Xử lý các LAYOUT đặc biệt (component là object)**
		if (legacyRoute.getComponent() instanceof Map) {
			try {
				// Sử dụng convertValue để an toàn, nhưng chỉ cần lấy 'name'
				Map<String, Object> compMap = objectMapper.convertValue(
						legacyRoute.getComponent(), new TypeReference<>() {
						}
				                                                       );
				String componentName = (String) compMap.get("name");
				
				if ("Layout".equals(componentName) || "AppMain".equals(componentName)) {
					// Nếu là Layout hoặc AppMain, trả về tên của chính nó.
					// Frontend sẽ tự biết cách render các layout này.
					return componentName;
				}
			} catch (Exception e) {
				log.warn("Không thể phân tích component object cho route '{}'. Lỗi: {}", legacyRoute.getName(), e.getMessage());
			}
		}
		
		// **QUY TẮC 3: Xử lý trường hợp component là chuỗi (ít gặp hơn)**
		if (legacyRoute.getComponent() instanceof String) {
			return (String) legacyRoute.getComponent();
		}
		
		// **FALLBACK:** Nếu không có quy tắc nào khớp, trả về null.
		// Tầng trên (mapRouteToMenuEntity) có thể sẽ gán một giá trị mặc định nếu cần.
		log.warn("Không thể xác định component cho route '{}'. Sẽ trả về null.", legacyRoute.getName());
		return null;
	}
	
}
