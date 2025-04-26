package vn.tr.core.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.tr.common.core.constant.Constants;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.feign.core.bean.FileDinhKem;
import vn.tr.common.feign.core.bean.FileList;
import vn.tr.core.dao.model.CoreAttachment;
import vn.tr.core.dao.service.CoreAttachmentService;
import vn.tr.core.data.CoreAttachmentData;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoreAttachmentBusiness {
	private final CoreAttachmentService coreAttachmentService;
	@Value("${core.attachment.host.download}")
	private String coreAttachmentHostDownload;
	@Value("${core.attachment.path.upload}")
	private String coreAttachmentPathUpload;
	@Value("${core.attachment.path.uploadtemp}")
	private String coreAttachmentPathUploadTemp;
	
	private CoreAttachmentData convertToCoreAttachmentData(CoreAttachment coreAttachment) {
		CoreAttachmentData coreAttachmentData = new CoreAttachmentData();
		coreAttachmentData.setId(coreAttachment.getId());
		coreAttachmentData.setAppCode(coreAttachment.getAppCode());
		coreAttachmentData.setBase64(coreAttachment.getBase64());
		coreAttachmentData.setCode(coreAttachment.getCode());
		coreAttachmentData.setFileName(coreAttachment.getFileName());
		coreAttachmentData.setFolder(coreAttachment.getFolder());
		coreAttachmentData.setLink(coreAttachment.getLink());
		coreAttachmentData.setMime(coreAttachment.getMime());
		coreAttachmentData.setMonth(coreAttachment.getMonth());
		coreAttachmentData.setObjectId(coreAttachment.getObjectId());
		coreAttachmentData.setSize(coreAttachment.getSize());
		coreAttachmentData.setType(coreAttachment.getType());
		coreAttachmentData.setYear(coreAttachment.getYear());
		return coreAttachmentData;
	}
	
	public Long copyAttachment(Long id, String appCode, Long objectId) {
		
		Optional<CoreAttachment> optional = coreAttachmentService.findByIdAndDaXoaFalse(id);
		if (optional.isPresent()) {
			CoreAttachment coreAttachment = optional.get();
			
			CoreAttachment newCoreAttachment = new CoreAttachment();
			newCoreAttachment.setMonth(coreAttachment.getMonth());
			newCoreAttachment.setYear(coreAttachment.getYear());
			newCoreAttachment.setFileName(coreAttachment.getFileName());
			newCoreAttachment.setSize(coreAttachment.getSize());
			newCoreAttachment.setMime(coreAttachment.getMime());
			newCoreAttachment.setFolder(coreAttachmentPathUploadTemp);
			newCoreAttachment.setAppCode(appCode);
			newCoreAttachment.setObjectId(objectId);
			String code = coreAttachment.getId() + coreAttachment.getFileName() + LocalDate.now();
			code = DigestUtils.md5Hex(code).toUpperCase();
			newCoreAttachment.setCode(code);
			coreAttachmentService.save(newCoreAttachment);
			
			coreAttachmentService.saveAndCopy(coreAttachment, newCoreAttachment);
			return newCoreAttachment.getId();
		}
		return null;
	}
	
	public void delete(Long id) throws EntityNotFoundException {
		Optional<CoreAttachment> optional = coreAttachmentService.findById(id);
		if (optional.isEmpty()) {
			throw new EntityNotFoundException(CoreAttachment.class, id);
		}
		CoreAttachment coreAttachment = optional.get();
		coreAttachment.setDaXoa(true);
		coreAttachmentService.save(coreAttachment);
	}
	
	public CoreAttachment doUpload(MultipartFile uploadfile) {
		String fileName = uploadfile.getOriginalFilename();
		CoreAttachment coreAttachment = new CoreAttachment();
		BufferedOutputStream stream = null;
		Path path = Paths.get(coreAttachmentPathUploadTemp);
		if (StringUtils.isNotBlank(fileName)) {
			int month;
			Calendar cal = Calendar.getInstance();
			Date date = new Date();
			cal.setTime(date);
			int year = LocalDate.now().getYear();
			month = cal.get(Calendar.MONTH) + 1;
			
			coreAttachment.setYear(year);
			coreAttachment.setMonth(month);
			coreAttachment.setFileName(fileName);
			coreAttachment.setSize(uploadfile.getSize());
			coreAttachment.setMime(uploadfile.getContentType());
			coreAttachment.setAppCode("");
			coreAttachment.setFolder(coreAttachmentPathUploadTemp);
			coreAttachment = coreAttachmentService.save(coreAttachment);
			String code = coreAttachment.getId() + coreAttachment.getFileName() + coreAttachment.getNgayTao().toString();
			code = DigestUtils.md5Hex(code).toUpperCase();
			coreAttachment.setCode(code);
			
			String link = coreAttachmentHostDownload + "/attachment/download/" + coreAttachment.getCode();
			coreAttachment.setLink(link);
			
			// base64
			StringBuilder base64Image = new StringBuilder("data:").append(uploadfile.getContentType()).append(";base64,");
			if (uploadfile.getContentType() != null && "image/jpeg".contains(uploadfile.getContentType()) ||
					"image/png".contains(uploadfile.getContentType())) {
				try {
					base64Image.append(Base64.getEncoder().encodeToString(uploadfile.getBytes()));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			coreAttachment.setBase64(base64Image.toString());
			coreAttachment = coreAttachmentService.save(coreAttachment);
			String filepath = Paths.get(coreAttachmentPathUploadTemp, code).toString();
			// Save the file locally
			try {
				Files.createDirectories(path);
				stream = new BufferedOutputStream(new FileOutputStream(filepath));
				stream.write(uploadfile.getBytes());
				stream.close();
			} catch (IOException e) {
				log.error("Lỗi xử lý file: " + e.getMessage());
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						log.error("Lỗi xử lý file: " + e.getMessage());
					}
				}
			}
			
		}
		return coreAttachment;
	}
	
	public void sign(Long fileDinhKemId, HttpServletResponse response) {
		JSONObject jsonObjectResult = new JSONObject();
		try {
			if (Objects.nonNull(fileDinhKemId)) {
				Optional<CoreAttachment> optionalCoreAttachment = coreAttachmentService.findById(fileDinhKemId);
				if (optionalCoreAttachment.isPresent()) {
					CoreAttachment coreAttachment = optionalCoreAttachment.get();
					
					JSONObject jsonKySo = new JSONObject();
					jsonKySo.set("tenFile", coreAttachment.getFileName());
					
					jsonObjectResult.set("FileName", coreAttachment.getFileName());
					jsonObjectResult.set("FileServer", jsonKySo.toString());
					jsonObjectResult.set("Status", true);
					jsonObjectResult.set("Message", "Thành công!");
					
					String filepath = Paths.get(coreAttachmentPathUploadTemp, coreAttachment.getCode()).toString();
					// Save the file locally
					BufferedInputStream in = new BufferedInputStream(new FileInputStream(filepath));
					BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filepath));
					byte[] buff = new byte[32 * 1024];
					int len = 0;
					while ((len = in.read(buff)) > 0)
						stream.write(buff, 0, len);
					in.close();
					stream.close();
				}
			}
			
			response.reset();
			response.resetBuffer();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(jsonObjectResult);
			response.flushBuffer();
			response.getWriter().close();
		} catch (Exception e) {
			log.error("Lỗi xử lý sign file:  {}", e.getMessage());
		}
		
	}
	
	public InputStreamResource download(String code) throws FileNotFoundException {
		Optional<CoreAttachment> optionalCoreAttachment = coreAttachmentService.findFirstByCode(code);
		if (optionalCoreAttachment.isPresent()) {
			CoreAttachment coreAttachment = optionalCoreAttachment.get();
			String path = Paths.get(coreAttachment.getFolder() + File.separator + coreAttachment.getCode()).toString();
			File file = new File(path);
			//			HttpHeaders respHeaders = new HttpHeaders();
			//			respHeaders.setContentType(MediaType.valueOf(coreAttachment.getMime()));
			//			respHeaders.setContentLength(file.length());
			//			respHeaders.setContentDispositionFormData("attachment", coreAttachment.getFileName());
			return new InputStreamResource(new FileInputStream(file));
		}
		return null;
	}
	
	public CoreAttachmentData findById(Long id) throws EntityNotFoundException {
		Optional<CoreAttachment> optionalCoreAttachment = coreAttachmentService.findByIdAndDaXoaFalse(id);
		if (optionalCoreAttachment.isEmpty()) {
			throw new EntityNotFoundException(CoreAttachment.class, id);
		}
		return convertToCoreAttachmentData(optionalCoreAttachment.get());
	}
	
	public FileDinhKem getAttachments(Long fileDinhKemId, String appCode, Long objectId, Integer type) {
		FileDinhKem fileDinhKem = new FileDinhKem();
		List<FileList> fileLists = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		if (type == Constants.DINH_KEM_NHIEU_FILE) {
			List<CoreAttachment> coreAttachments = coreAttachmentService.findByObjectIdAndAppCodeAndTypeAndDaXoaFalse(objectId, appCode, type);
			if (CollUtil.isNotEmpty(coreAttachments)) {
				for (CoreAttachment coreAttachment : coreAttachments) {
					FileList fileList = new FileList();
					fileList.setId(coreAttachment.getId());
					fileList.setName(coreAttachment.getFileName());
					fileList.setUrl(coreAttachment.getLink());
					ids.add(coreAttachment.getId());
					fileLists.add(fileList);
				}
			}
		} else if (type == Constants.DINH_KEM_1_FILE && Objects.nonNull(fileDinhKemId)) {
			Optional<CoreAttachment> optionalAttachment = coreAttachmentService.findByIdAndDaXoaFalse(fileDinhKemId);
			if (optionalAttachment.isPresent()) {
				FileList fileList = new FileList();
				fileList.setId(optionalAttachment.get().getId());
				fileList.setName(optionalAttachment.get().getFileName());
				fileList.setUrl(optionalAttachment.get().getLink());
				ids.add(optionalAttachment.get().getId());
				fileLists.add(fileList);
			}
		}
		fileDinhKem.setIds(ids);
		fileDinhKem.setFileLists(fileLists);
		return fileDinhKem;
	}
	
	public String getBase64(String urlString) {
		try {
			URL url = new URI(urlString).toURL();
			
			InputStream is = url.openStream();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[4];
			
			while ((nRead = is.readNBytes(data, 0, data.length)) != 0) {
				buffer.write(data, 0, nRead);
			}
			
			buffer.flush();
			byte[] bytes = buffer.toByteArray();
			is.close();
			return Base64.getEncoder().encodeToString(bytes);
		} catch (Exception e) {
			return null;
		}
	}
	
	public CoreAttachmentData getById(Long id) {
		Optional<CoreAttachment> optionalCoreAttachment = coreAttachmentService.findByIdAndDaXoaFalse(id);
		return optionalCoreAttachment.map(this::convertToCoreAttachmentData).orElseGet(CoreAttachmentData::new);
	}
	
	public List<Long> saveAttachments(List<Long> fileDinhKemIds, String appCode, long objectId, int type) {
		List<Long> ids = new ArrayList<>();
		if (CollUtil.isNotEmpty(fileDinhKemIds)) {
			for (Long fileDinhKemId : fileDinhKemIds) {
				CoreAttachment coreAttachment = new CoreAttachment();
				Optional<CoreAttachment> optionalCoreAttachment = coreAttachmentService.findById(fileDinhKemId);
				if (optionalCoreAttachment.isPresent()) {
					coreAttachment = optionalCoreAttachment.get();
				}
				coreAttachment.setDaXoa(false);
				coreAttachment.setAppCode(appCode);
				coreAttachment.setObjectId(objectId);
				coreAttachment.setType(type);
				coreAttachment = coreAttachmentService.save(coreAttachment);
				
				coreAttachmentService.saveAndMove(coreAttachment);
				log.info("coreAttachment id : {}", coreAttachment.getId());
				ids.add(coreAttachment.getId());
				/* thoát nếu đính kèm 1 file */
				if (type == Constants.DINH_KEM_1_FILE) {
					break;
				}
			}
		}
		return ids;
	}
	
}
