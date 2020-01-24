package sample;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Controller {
    public int blockSize;
    public int codeBookSize;
    public String imagePath;
    public String compressedPath;
    public VectorQuantization obj = new VectorQuantization();
    @FXML
    private TextField blockSizeTxt;
    @FXML
    private TextField codeBookSizeTxt;
    @FXML
    private ImageView originalImage;
    @FXML
    private ImageView compressedImage;
    @FXML
    private void chooseImage()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("E:\\"));
        Stage window = new Stage();
        File file = fileChooser.showOpenDialog(window);
        if(file!=null) {
            imagePath = file.toString();

            getData();
            passingData();
            showOriginalImage();
            obj.read();
            obj.loadImageTo2DMatrix();

            // obj.printMatrix();
            // obj.constructImageWithSpllitedMatrix(obj.generateCodeBookPixels(obj.getCodeBook()));
            obj.compression();
            compressedPath = obj.decompression();
            showCompressedImage();
        }

    }
    private void getData()
    {
        blockSize = Integer.parseInt(blockSizeTxt.getText());
        codeBookSize = Integer.parseInt(codeBookSizeTxt.getText());
    }
    private void showOriginalImage()
    {
        try {
            Image image = new Image(new FileInputStream(imagePath));
            originalImage.setImage(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void showCompressedImage()
    {
        try {
        Image image = new Image(new FileInputStream(compressedPath));
        compressedImage.setImage(image);
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
    }
    private void passingData()
    {

        obj.setBlockSize(blockSize);
        obj.setCodeBookSize(codeBookSize);
        obj.setImagePath(imagePath);
    }
}
