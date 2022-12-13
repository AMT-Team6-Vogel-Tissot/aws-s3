package heig.vd.s3.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@NoArgsConstructor
@Accessors(chain=true)
public class AppRequest {
    @NotBlank
    private String objectName;
    @NotBlank
    private byte[] contentFile;
}
