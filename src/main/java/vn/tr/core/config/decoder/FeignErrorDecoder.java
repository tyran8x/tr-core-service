package vn.tr.core.config.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

public class FeignErrorDecoder implements ErrorDecoder {
	
	@Override
	public Exception decode(String methodKey, Response response) {
		
		return switch (response.status()) {
			case 400 -> new BadRequestException();
			case 401 -> new Exception("Unauthorized");
			case 404 -> new NotFoundException();
			default -> new Exception("Generic error");
		};
	}
	
}

