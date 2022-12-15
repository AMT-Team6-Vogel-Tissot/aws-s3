package heig.vd.s3.exception;

import java.util.Date;

public record ErrorMessage(int statusCode, Date timestamp, String message) {

}
