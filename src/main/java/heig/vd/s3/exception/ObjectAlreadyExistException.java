package heig.vd.s3.exception;

public class ObjectAlreadyExistException extends RuntimeException{
    public ObjectAlreadyExistException(String message) {
        super("Le bucket n'existe pas: " + message);
    }
}
