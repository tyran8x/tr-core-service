package vn.tr.core.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import vn.tr.common.core.exception.ServiceException;
import vn.tr.common.core.utils.SpringUtils;
import vn.tr.common.core.utils.ValidatorUtils;
import vn.tr.common.excel.core.ExcelListener;
import vn.tr.common.excel.core.ExcelResult;
import vn.tr.common.satoken.utils.LoginHelper;
import vn.tr.core.dao.model.CoreUser;
import vn.tr.core.dao.service.CoreConfigService;
import vn.tr.core.dao.service.CoreUserService;
import vn.tr.core.data.dto.CoreUserImportData;

import java.util.List;
import java.util.Optional;

@Slf4j

public class CoreUserImportListener extends AnalysisEventListener<CoreUserImportData> implements ExcelListener<CoreUserImportData> {
	
	private final CoreUserService coreUserService;
	
	private final String password;
	
	private final Boolean isUpdateSupport;
	
	private final String operUsername;
	private final StringBuilder successMsg = new StringBuilder();
	private final StringBuilder failureMsg = new StringBuilder();
	private int successNum = 0;
	private int failureNum = 0;
	
	public CoreUserImportListener(Boolean isUpdateSupport) {
		String initPassword = SpringUtils.getBean(CoreConfigService.class).getString("sys.user.initPassword", null).get();
		this.coreUserService = SpringUtils.getBean(CoreUserService.class);
		this.password = BCrypt.hashpw(initPassword);
		this.isUpdateSupport = isUpdateSupport;
		this.operUsername = LoginHelper.getUsername();
	}
	
	@Override
	public void invoke(CoreUserImportData coreUserImportData, AnalysisContext context) {
		Optional<CoreUser> optionalCoreUser = this.coreUserService.findFirstByEmailIgnoreCase(coreUserImportData.getUsername());
		try {
			if (optionalCoreUser.isEmpty()) {
				CoreUser user = BeanUtil.toBean(coreUserImportData, CoreUser.class);
				ValidatorUtils.validate(user);
				user.setHashedPassword(password);
				user.setCreatedBy(operUsername);
				coreUserService.save(user);
				successNum++;
				successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUsername()).append(" 导入成功");
			} else if (isUpdateSupport) {
				CoreUser sysUser = optionalCoreUser.get();
				CoreUser user = BeanUtil.toBean(coreUserImportData, CoreUser.class);
				
				ValidatorUtils.validate(user);
//				coreUserService.checkUserAllowed(user.getUserId());
//				coreUserService.checkUserDataScope(user.getUserId());
				user.setUpdatedBy(operUsername);
				coreUserService.save(user);
				successNum++;
				successMsg.append("<br/>").append(successNum).append("、account ").append(user.getUsername()).append(" Update successfully");
			} else {
				failureNum++;
				failureMsg.append("<br/>")
						.append(failureNum)
						.append("、account ")
						.append(optionalCoreUser.get().getUsername())
						.append(" Already exists");
			}
		} catch (Exception e) {
			failureNum++;
			String msg = "<br/>" + failureNum + "、account " + coreUserImportData.getUsername() + " Import failed：";
			failureMsg.append(msg).append(e.getMessage());
			log.error(msg, e);
		}
	}
	
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
	
	}
	
	@Override
	public ExcelResult<CoreUserImportData> getExcelResult() {
		return new ExcelResult<>() {
			
			@Override
			public List<CoreUserImportData> getList() {
				return null;
			}
			
			@Override
			public List<String> getErrorList() {
				return null;
			}
			
			@Override
			public String getAnalysis() {
				if (failureNum > 0) {
					failureMsg.insert(0,
							"Sorry, the import failed! common " + failureNum + " The data format is incorrect, the error is as follows：");
					throw new ServiceException(failureMsg.toString());
				} else {
					successMsg.insert(0,
							"Congratulations, all the data has been imported successfully! common " + successNum + " The data is as follows：");
				}
				return successMsg.toString();
			}
		};
	}
}
