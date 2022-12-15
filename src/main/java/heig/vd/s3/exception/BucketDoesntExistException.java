package heig.vd.s3.exception;

public class BucketDoesntExistException extends RuntimeException{
    public BucketDoesntExistException(String message) {
        super("Le bucket n'existe pas: " + message);
    }
}
