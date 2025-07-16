package vn.tr.core.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.tr.common.core.constant.CacheConstants;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.domain.data.UserOnlineDTO;
import vn.tr.common.core.utils.StreamUtils;
import vn.tr.common.core.utils.StringUtils;
import vn.tr.common.log.annotation.Log;
import vn.tr.common.log.enums.BusinessType;
import vn.tr.common.redis.utils.RedisUtils;
import vn.tr.common.web.core.BaseController;
import vn.tr.core.data.UserOnlineData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/online")
public class CoreUserOnlineController extends BaseController {
	
	@SaCheckPermission("monitor:online:list")
	@GetMapping("/list")
	public R<List<UserOnlineData>> list(String ipaddr, String userName) {
		// Get all unexpired tokens
		List<String> keys = StpUtil.searchTokenValue("", 0, -1, false);
		List<UserOnlineDTO> userOnlineDTOList = new ArrayList<>();
		for (String key : keys) {
			String token = StringUtils.substringAfterLast(key, ":");
			// Skip if expired
			if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
				continue;
			}
			userOnlineDTOList.add(RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token));
		}
		if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
			userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
							StringUtils.equals(ipaddr, userOnline.getIpaddr()) &&
									StringUtils.equals(userName, userOnline.getUsername())
			                                      );
		} else if (StringUtils.isNotEmpty(ipaddr)) {
			userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
							StringUtils.equals(ipaddr, userOnline.getIpaddr())
			                                      );
		} else if (StringUtils.isNotEmpty(userName)) {
			userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
							StringUtils.equals(userName, userOnline.getUsername())
			                                      );
		}
		Collections.reverse(userOnlineDTOList);
		userOnlineDTOList.removeAll(Collections.singleton(null));
		List<UserOnlineData> userOnlineList = BeanUtil.copyToList(userOnlineDTOList, UserOnlineData.class);
		return R.ok(userOnlineList);
	}
	
	@SaCheckPermission("monitor:online:forceLogout")
	@Log(title = "Online users", businessType = BusinessType.FORCE)
	@DeleteMapping("/{tokenId}")
	public R<Void> forceLogout(@PathVariable String tokenId) {
		try {
			StpUtil.kickoutByTokenValue(tokenId);
		} catch (NotLoginException ignored) {
		}
		return R.ok();
	}
	
	@GetMapping()
	public R<List<UserOnlineData>> getInfo() {
		List<String> tokenIds = StpUtil.getTokenValueListByLoginId(StpUtil.getLoginIdAsString());
		List<UserOnlineDTO> userOnlineDTOList = tokenIds.stream()
				.filter(token -> StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) >= -1)
				.map(token -> (UserOnlineDTO) RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token))
				.collect(Collectors.toList());
		Collections.reverse(userOnlineDTOList);
		userOnlineDTOList.removeAll(Collections.singleton(null));
		List<UserOnlineData> userOnlineList = BeanUtil.copyToList(userOnlineDTOList, UserOnlineData.class);
		return R.ok(userOnlineList);
	}
	
	@Log(title = "Online Device", businessType = BusinessType.FORCE)
	@PostMapping("/{tokenId}")
	public R<Void> remove(@PathVariable("tokenId") String tokenId) {
		try {
			// Get the token collection of the specified account ID
			List<String> keys = StpUtil.getTokenValueListByLoginId(StpUtil.getLoginIdAsString());
			keys.stream()
					.filter(key -> key.equals(tokenId))
					.findFirst()
					.ifPresent(key -> StpUtil.kickoutByTokenValue(tokenId));
		} catch (NotLoginException ignored) {
		}
		return R.ok();
	}
	
}
