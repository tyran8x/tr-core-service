package vn.tr.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.tr.common.core.domain.R;
import vn.tr.core.business.CoreWebhookBussiness;
import vn.tr.core.data.zalo.ZaloWebhookData;

@RestController
@RequestMapping(value = "/webhook")
@RequiredArgsConstructor
@Slf4j
public class CoreWebHookController {
	
	private final CoreWebhookBussiness coreWebhookBussiness;
	
	@PostMapping("/zalo")
	public R<Void> zalo(@RequestBody ZaloWebhookData zaloWebhookData) {
		coreWebhookBussiness.getMessageZaloOa(zaloWebhookData);
		return R.ok();
	}
	
	//	@GetMapping(value = "/images", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	//	public @ResponseBody Resource getFileViaByteArrayResource(String ) throws IOException, URISyntaxException {
	//		Java2DRenderer renderer = new Java2DRenderer(
	//				input.toURI().toURL().toString(), 800, 600);
	//
	//		// Render the HTML content as a BufferedImage
	//		BufferedImage image = renderer.getImage();
	//
	//		// Save the rendered image to a PNG file
	//		File output = new File("path/to/output.png");
	//		ImageIO.write(image, "png", output);
	//	}
	
}
