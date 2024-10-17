//package vn.tr.core.controller;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//public class SecuredController {
//
//	private Logger logger = LoggerFactory.getLogger(SecuredController.class);
//
//	@GetMapping("/secured")
//	public String securedIndex(ModelMap modelMap) {
//
//		logger.info("/secured called");
//
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//		if (authentication.getPrincipal() instanceof UserDetails)
//			modelMap.put("username", ((UserDetails) authentication.getPrincipal()).getUsername());
//
//		return "secure/index";
//	}
//
//	@GetMapping("/token")
//	public ResponseEntity<?> getToken(@AuthenticationPrincipal UserDetails principal) {
//		return ResponseEntity.ok(principal.getUsername());
//	}
//}
