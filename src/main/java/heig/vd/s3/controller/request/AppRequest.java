package heig.vd.s3.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain=true)
public class AppRequest {
    private String objectName;
    private byte[] contentFile;
}
