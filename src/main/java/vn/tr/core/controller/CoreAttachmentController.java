package vn.tr.core.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.tr.common.core.domain.R;
import vn.tr.common.core.exception.base.EntityNotFoundException;
import vn.tr.common.feign.core.bean.FileDinhKem;
import vn.tr.core.business.CoreAttachmentBusiness;
import vn.tr.core.dao.model.CoreAttachment;
import vn.tr.core.dao.service.CoreAttachmentService;
import vn.tr.core.data.CoreAttachmentData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class CoreAttachmentController {
	
	private final CoreAttachmentService coreAttachmentService;
	private final CoreAttachmentBusiness coreAttachmentBusiness;
	
	@GetMapping(value = {"/copy"})
	public R<Long> copyAttachment(@RequestParam(value = "fileDinhKemId") Long fileDinhKemId,
			@RequestParam(value = "appCode") String appCode, @RequestParam(value = "objectId") Long objectId) {
		return R.ok(coreAttachmentBusiness.copyAttachment(fileDinhKemId, appCode, objectId));
	}
	
	@DeleteMapping(value = {"/{id}"})
	public R<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
		coreAttachmentBusiness.delete(id);
		return R.ok();
	}
	
	@PostMapping(value = {"/doupload"})
	public ResponseEntity<CoreAttachment> doUpload(@RequestParam("uploadfile") MultipartFile uploadfile) {
		return ResponseEntity.ok(coreAttachmentBusiness.doUpload(uploadfile));
	}
	
	@PostMapping(value = {"/download/sign"})
	public void sign(@RequestParam("uploadfile") MultipartFile uploadfile, HttpServletResponse response) {
		coreAttachmentBusiness.sign(uploadfile, response);
	}
	
	@PostMapping(value = {"/download/signv2"})
	public void sign(@RequestParam("fileId") Long fileId, HttpServletResponse response) {
		coreAttachmentBusiness.signByFile(fileId, response);
	}
	
	@GetMapping(value = "/download/{code}")
	public ResponseEntity<InputStreamResource> download(@PathVariable String code) throws IOException {
		Optional<CoreAttachment> optionalCoreAttachment = coreAttachmentService.findFirstByCode(code);
		if (optionalCoreAttachment.isPresent()) {
			CoreAttachment coreAttachment = optionalCoreAttachment.get();
			String path = Paths.get(coreAttachment.getFolder() + File.separator + coreAttachment.getCode()).toString();
			File file = new File(path);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders.setContentType(MediaType.valueOf(coreAttachment.getMime()));
			respHeaders.setContentLength(file.length());
			respHeaders.setContentDispositionFormData("attachment", coreAttachment.getFileName());
			InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
			return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
		}
		return null;
	}
	
	@GetMapping(value = "/{id}")
	public R<CoreAttachmentData> findById(@PathVariable("id") Long id) throws EntityNotFoundException {
		CoreAttachmentData coreAttachmentData = coreAttachmentBusiness.findById(id);
		return R.ok(coreAttachmentData);
	}
	
	@GetMapping(value = {"/get"})
	public R<FileDinhKem> getAttachments(
			@RequestParam(value = "fileDinhKemId", required = false) Long fileDinhKemId, @RequestParam(value = "appCode") String appCode,
			@RequestParam(value = "objectId", required = false) Long objectId, @RequestParam(value = "type") Integer type) {
		return R.ok(coreAttachmentBusiness.getAttachments(fileDinhKemId, appCode, objectId, type));
	}
	
	@GetMapping(value = {"/getBase64"})
	public R<String> getBase64(@RequestParam(value = "urlFile") String urlFile) {
		return R.ok(coreAttachmentBusiness.getBase64(urlFile));
	}
	
	@GetMapping(value = "/get/id")
	public R<CoreAttachmentData> getById(@RequestParam(name = "id") Long id) {
		CoreAttachmentData coreAttachmentData = coreAttachmentBusiness.getById(id);
		return R.ok(coreAttachmentData);
	}
	
	@GetMapping(value = {"/save"})
	public R<List<Long>> saveAttachments(
			@RequestParam(value = "fileDinhKemIds", required = false) List<Long> fileDinhKemIds, @RequestParam(value = "appCode") String appCode,
			@RequestParam(value = "objectId") long objectId, @RequestParam(value = "type") int type) {
		return R.ok(coreAttachmentBusiness.saveAttachments(fileDinhKemIds, appCode, objectId, type));
	}
}
