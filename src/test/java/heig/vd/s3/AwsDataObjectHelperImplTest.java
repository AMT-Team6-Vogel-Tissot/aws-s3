package heig.vd.s3;

import static org.junit.jupiter.api.Assertions.*;

import heig.vd.s3.exception.ObjectNotFoundException;
import heig.vd.s3.service.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AwsDataObjectHelperImplTest {

    private final S3Service s3Service = new S3Service();
    private final String pathToTestFolder = "./filesTest/";
    private final String image1 = "file1.jpg";
    private final String image2 = "file2.jpg";
    private final String fileText = "file.txt";
    private final String newImageName = "file3.jpg";

    private final Path path1 = Path.of(this.pathToTestFolder, this.image1);
    private final Path path2 = Path.of(this.pathToTestFolder, this.image2);
    private final Path path3 = Path.of(this.pathToTestFolder, this.fileText);

    @AfterEach
    public void cleanUpEach(){
        if(this.s3Service.exist(this.image1)){
            this.s3Service.delete(this.image1);
        }
        if(this.s3Service.exist(this.image2)){
            this.s3Service.delete(this.image2);
        }
        if(this.s3Service.exist(this.newImageName)){
            this.s3Service.delete(this.newImageName);
        }
        if(this.s3Service.exist(this.fileText)){
            this.s3Service.delete(this.fileText);
        }
    }


    @Disabled
    @Test
    public void ListBucket_Success() {

        // given
        String listBuckets = "amt.team01.diduno.education\n" +
                "amt.team02.diduno.education\n" +
                "amt.team03.diduno.education\n" +
                "amt.team04.diduno.education\n" +
                "amt.team05.diduno.education\n" +
                "amt.team06.diduno.education\n" +
                "amt.team07.diduno.education\n" +
                "amt.team08.diduno.education\n" +
                "amt.team09.diduno.education\n" +
                "amt.team10.diduno.education\n" +
                "amt.team11.diduno.education\n" +
                "amt.team98.diduno.education\n" +
                "amt.team99.diduno.education\n" +
                "raid0toraid5.diduno.education\n" +
                "raid1toraid6.diduno.education\n" +
                "raid5toraid0.diduno.education\n" +
                "raid6toraid1.diduno.education\n" +
                "sto1.team.00\n" +
                "test.bucket.sto1\n";
        String actualResult;

        // when
        actualResult = this.s3Service.list();

        // then
        assertEquals(listBuckets, actualResult);
    }

    @Disabled
    @Test
    public void List_ListAllObjects_Success() throws IOException {
        // given
        String listObjects = "file1.jpg\n" + "file2.jpg\n";

        this.s3Service.create(this.image1, Files.readAllBytes(this.path1));
        this.s3Service.create(this.image2, Files.readAllBytes(this.path2));
        String actualResult;

        // when
        actualResult = this.s3Service.list();

        // then
        assertEquals(listObjects, actualResult);
    }

    @Test
    public void Exist_bucketExist_Success() {
        // given
        boolean actualResult;

        // when
        actualResult = this.s3Service.exist();

        // then
        assertTrue(actualResult);
    }

    @Test
    public void Exist_ObjectIsPresent_Success() throws IOException {
        // given
        this.s3Service.create(this.image1, Files.readAllBytes(this.path1));
        boolean actualResult;

        // when
        actualResult = this.s3Service.exist(this.image1);

        // then
        this.s3Service.delete(this.image1);
        assertTrue(actualResult);
    }

    @Test
    public void Exist_ObjectIsNotPresent_Success() {
        // given
        assertTrue(this.s3Service.exist());
        boolean actualResult;

        // when
        actualResult = this.s3Service.exist(this.image1);

        // then
        assertFalse(actualResult);
    }

    @Test
    public void Create_CreateObjectWithExistingBucket_Success() throws IOException {
        // given
        assertTrue(this.s3Service.exist());
        assertFalse(this.s3Service.exist(this.image1));

        // when
        this.s3Service.create(this.image1, Files.readAllBytes(this.path1));

        // then
        assertTrue(this.s3Service.exist(this.image1));
    }

    @Test
    public void Delete_RemoveNotExistingObject_Success() {
        // given
        assertTrue(this.s3Service.exist());

        // when
        assertThrows(ObjectNotFoundException.class, () -> this.s3Service.delete(this.image1));
        // then
    }

    @Test
    public void Delete_NotEmptyBucket_Success() throws IOException {
        // given
        this.s3Service.create(this.image1, Files.readAllBytes(this.path1));

        assertTrue(this.s3Service.exist());
        assertTrue(this.s3Service.exist(this.image1));

        // when
        this.s3Service.delete(this.image1);

        // then
        assertFalse(this.s3Service.exist(this.image1));
    }

    @Test
    public void Update_UpdateExistingObjectContent_Success() throws IOException {
        // given
        assertTrue(this.s3Service.exist());
        String exceptedValue = "Test file";

        this.s3Service.create(this.image1, Files.readAllBytes(this.path1));
        assertTrue(this.s3Service.exist(this.image1));

        // when
        this.s3Service.update(this.image1, Files.readAllBytes(this.path3));
        String value = new String(this.s3Service.get(this.image1));

        // then
        assertTrue(this.s3Service.exist(this.image1));
        assertEquals(exceptedValue, value);
    }

    @Test
    public void Update_UpdateExistingObjectNameAndContent_Success() throws IOException {
        // given
        assertTrue(this.s3Service.exist());
        String exceptedValue = "Test file";

        this.s3Service.create(this.image1, Files.readAllBytes(this.path1));
        assertTrue(this.s3Service.exist(this.image1));

        // when
        this.s3Service.update(this.image1, Files.readAllBytes(this.path3), this.newImageName);
        String value = new String(this.s3Service.get(this.newImageName));

        // then
        assertFalse(this.s3Service.exist(this.image1));
        assertTrue(this.s3Service.exist(this.newImageName));
        assertEquals(exceptedValue, value);
    }

    @Test
    public void Get_GetExistingObject_Success() throws IOException {
        // given
        assertTrue(this.s3Service.exist());
        this.s3Service.create(this.fileText, Files.readAllBytes(this.path3));
        assertTrue(this.s3Service.exist(this.fileText));
        String exceptedValue = "Test file";

        // when
        String value = new String(this.s3Service.get(this.fileText));

        // then
        assertEquals(exceptedValue, value);
    }

    @Test
    public void Get_GetNotExistingObject_Failure() {
        // given
        assertTrue(this.s3Service.exist());

        // when
        assertThrows(ObjectNotFoundException.class, () -> this.s3Service.get(this.fileText));
    }

}