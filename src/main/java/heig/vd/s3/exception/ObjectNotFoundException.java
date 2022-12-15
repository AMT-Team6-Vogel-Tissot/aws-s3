package heig.vd.s3.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String name) {
        super("L'image n'existe pas: " + name);
    }
}
