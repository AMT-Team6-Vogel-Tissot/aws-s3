package heig.vd.s3.exception;

public class FileUploadException extends RuntimeException {

    public FileUploadException(String message) {

        super("Un problème est survenu lors de l'upload du fichier : " + message);
        }

}
