package vn.tr.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "core")
public class CoreProperties {

    private Attachment attachment = new Attachment();

    @Data
    public static class Attachment {

        private Host host = new Host();
        private AttachmentPath path = new AttachmentPath();

        @Data
        public static class Host {

            private String download;
        }

        @Data
        public static class AttachmentPath {

            private String upload;
            private String uploadtemp;
        }
    }
}
