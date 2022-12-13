package heig.vd.s3.exception;

public class FileException extends RuntimeException {

    public FileException(String message) {

        super("Un problème est survenu lors de l'upload du fichier : " + message);
        }

}
