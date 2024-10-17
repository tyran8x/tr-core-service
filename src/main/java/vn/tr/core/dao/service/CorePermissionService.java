package vn.tr.core.dao.service;

import java.util.Set;

public interface CorePermissionService {

	Set<String> getRolePermission(String userName);

	Set<String> getMenuPermission(String userName);

}
