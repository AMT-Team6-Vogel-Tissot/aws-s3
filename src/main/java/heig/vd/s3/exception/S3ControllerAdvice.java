package heig.vd.s3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ControllerAdvice
public class S3ControllerAdvice {

    @ResponseBody
    @ExceptionHandler(BucketDoesntExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage bucketDoesntExistException(BucketDoesntExistException e){
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), new Date(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage imageNotFound(ObjectNotFoundException e){

        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), new Date(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ObjectAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage imageAlreadyExist(ObjectAlreadyExistException e){

        return new ErrorMessage(HttpStatus.CONFLICT.value(), new Date(), e.getMessage());
    }


}
