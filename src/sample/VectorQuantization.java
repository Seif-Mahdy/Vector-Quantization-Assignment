package sample;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.StrictMath.pow;

public class VectorQuantization {
    private int blockSize;
    private int codeBookSize;
    private String imagePath;
    BufferedImage image;
    private  int[][] matrix;
    private ArrayList<codeBlock>codeBook;
    private ArrayList<ArrayList<Integer>>spittedMatrix;



    public void read()
    {
        File file =new File(imagePath);
        image = null;
        try {
            image= ImageIO.read(file);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void generateCodeBookCodes()
    {
        for(int i=0;i<codeBook.size();i++)
        {
            codeBook.get(i).setCode(Integer.toBinaryString(i));
        }
    }

    public ArrayList<ArrayList<Integer>> calculateAverageOfIndexes(ArrayList<ArrayList<Integer>>indexes)
    {
        ArrayList<ArrayList<Integer>>block;
        ArrayList<ArrayList<Integer>>averageBlocks=new ArrayList<>();
        for(int i=0;i<indexes.size();i++)
        {
            block=new ArrayList<>();
         for(int j=0;j<indexes.get(i).size();j++)
         {
                block.add(spittedMatrix.get(indexes.get(i).get(j)));
         }
         if(block.size()>0) {
             averageBlocks.add(calculateAvg(block));
         }
         }
        return averageBlocks;
    }
    public ArrayList<ArrayList<Integer>> splitAverageBlocks(ArrayList<ArrayList<Integer>> averageBlocks)
    {
        ArrayList<ArrayList<Integer>>newAverageBlocks=new ArrayList<>();
        ArrayList<Integer>roundedAverageBlock;
        ArrayList<Integer>ceilledAverageBlock;
        for(int i=0;i<averageBlocks.size();i++)
        {
            roundedAverageBlock=new ArrayList<>();
            ceilledAverageBlock=new ArrayList<>();
            for(int j=0;j<averageBlocks.get(i).size();j++)
            {
                roundedAverageBlock.add(averageBlocks.get(i).get(j)-1);
            }

            for (int j=0;j<averageBlocks.get(i).size();j++)
            {
                ceilledAverageBlock.add(averageBlocks.get(i).get(j)+1);
            }
            newAverageBlocks.add(roundedAverageBlock);
            newAverageBlocks.add(ceilledAverageBlock);
        }
        return newAverageBlocks;
    }
    public ArrayList<ArrayList<Integer>> associateBlocks(ArrayList<ArrayList<Integer>>averageBlocks)
    {
        ArrayList<ArrayList<Integer>>blockIndexes=new ArrayList<>();
        for(int i=0;i<averageBlocks.size();i++)
        {
            blockIndexes.add(new ArrayList<>());
        }
        int minDistance= 0;
        int index=0;
        int temp=0;
        for(int i=0;i<spittedMatrix.size();i++)
     {
         minDistance=calculateDistance(spittedMatrix.get(i), averageBlocks.get(0));
        for(int j=1;j<averageBlocks.size();j++) {
               temp= calculateDistance(spittedMatrix.get(i), averageBlocks.get(j));
            if(minDistance >temp)
            {
                minDistance=temp;
                index=j;
            }

        }
        blockIndexes.get(index).add(i);
        index=0;
     }
        return blockIndexes;
    }
    public void createCodeBook()
    {
        ArrayList<ArrayList<Integer>>averageBlocks=new ArrayList<>();
        ArrayList<ArrayList<Integer>>blockIndexes;
        ArrayList<Integer>averageBlock;
        spittedMatrix=splitMatrix();

        for(int i=0;i<=(Math.log(codeBookSize)/Math.log(2));i++)
        {
            if(i==0)
            {
                averageBlock=calculateAvg(spittedMatrix);
                averageBlocks.add(averageBlock);
            }
            else {
                averageBlocks = splitAverageBlocks(averageBlocks);
                blockIndexes = associateBlocks(averageBlocks);
                averageBlocks = calculateAverageOfIndexes(blockIndexes);
            }
            }
        codeBlock obj;

        codeBook=new ArrayList<>();
        for(int i=0;i<averageBlocks.size();i++)
        {
            obj= new codeBlock(averageBlocks.get(i));
            codeBook.add(obj);
        }
        generateCodeBookCodes();
        writeCodeBook();
    /*for(int i=0;i<codeBook.size();i++)
    {
        System.out.println("size"+ codeBook.size());
        System.out.println("index"+ i );
        System.out.println("code "+ codeBook.get(i).getCode());
    }*/

    }
    public void writeCodeBook()
    {FileOutputStream output=null;

        try {
             output=new FileOutputStream("F:\\FCI\\3rd YEAR\\testing\\codeBook.txt");
            output.write((codeBook.size()+"\n").getBytes());
            for(int i=0;i<codeBook.size();i++)
            {
                try {

                    output.write((codeBook.get(i).getCode()+"\n").getBytes());
                    for(int j=0;j<codeBook.get(i).getPixels().size();j++)
                    {
                        output.write(((codeBook.get(i).getPixels().get(j)).toString()+"\n").getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public void loadCodeBook()
    {
        ArrayList<Integer>block;
        codeBlock obj;
        String blockCode;
        try {
            Scanner input=new Scanner(new File("F:\\FCI\\3rd YEAR\\testing\\codeBook.txt"));
            while (input.hasNext())
            {
                int codeBookSize=input.nextInt();
                for(int i=0;i<codeBookSize;i++)
                {
                    blockCode=input.next();
                    block=new ArrayList<>();
                    obj=new codeBlock();
                    for(int j=0;j<blockSize*blockSize;j++)
                    {
                        block.add(input.nextInt());
                    }
                    obj.setCode(blockCode);
                    obj.setPixels(block);
                    codeBook.add(obj);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    public void compression()
    {
        createCodeBook();
        ArrayList<ArrayList<Integer>>codeBookPixels;
        codeBookPixels=generateCodeBookPixels(codeBook);
        ArrayList<ArrayList<Integer>>codeBookIndexes=new ArrayList<>();
        codeBookIndexes=associateBlocks(codeBookPixels);
        FileOutputStream output = null;
        try {
             output=new FileOutputStream("F:\\FCI\\3rd YEAR\\testing\\compressionTable.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for(int i=0;i<codeBookIndexes.size();i++)
        {
            for(int j=0;j<codeBookIndexes.get(i).size();j++)
            {
               // System.out.println( " code Book "+  i +" index in spittedMatrix  "+codeBookIndexes.get(i).get(j));
                try {
                    output.write((codeBookIndexes.get(i).get(j)+" "+codeBook.get(i).getCode()+"\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writeCodeBook();
        System.out.println("compression:Done");

    }
    public String decompression()
    {   int matrixIndex=0;
    String codeBookIndex;
    loadCodeBook();
    ArrayList<Integer>block;
        try {
            Scanner input=new Scanner(new File("F:\\FCI\\3rd YEAR\\testing\\compressionTable.txt"));
            while (input.hasNext()) {
                block=new ArrayList<>();
                matrixIndex = input.nextInt();
                codeBookIndex = input.next();
                for(int i=0;i<codeBook.size();i++)
                {
                    if(codeBookIndex.equals(codeBook.get(i).getCode()))
                    {
                        block=codeBook.get(i).getPixels();
                    }
                }
                spittedMatrix.set(matrixIndex,block);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String path=constructImageWithSpllitedMatrix(spittedMatrix);
        System.out.println("Decompression:Done");
        return path;


    }
    public ArrayList<ArrayList<Integer>> splitMatrix()
    {
        ArrayList<ArrayList<Integer>>splittedMatrix=new ArrayList<>();
        ArrayList<Integer>block;
        int size=((image.getHeight()*image.getWidth())/(blockSize*blockSize));

        for(int numberOfBlocks=0,row=0,col=0;(numberOfBlocks<size);numberOfBlocks++)
        {

            block=new ArrayList<>();
            for(int pixel=0;pixel<(blockSize*blockSize);pixel++)
            {
                // 3la mostwa elblock
                block.add(matrix[col][row]);

                if((row+1)%blockSize==0 && (!((col+1)%blockSize==0)))
                {
                    col++;
                    row=row-(blockSize-1);
                }
                else
                {
                    row++;
                }
                //    System.out.println("pixel "+  pixel);

            }
            splittedMatrix.add(block);
            // System.out.println("entered");
            if((row)==image.getHeight())
            {
                row=0;
                col++;
            }
            else
            {
                col=col-(blockSize-1);
            }

        }

        return splittedMatrix;
    }

    public  void loadImageTo2DMatrix()
    {
        int width=image.getWidth();
        int height=image.getHeight();
        matrix= new int[width][height];
        // System.out.println("height  "+height);
        //System.out.println("width  "+width);
        int value=0;
        int counter=0;
        for(int col=0;col<width;col++)
        {
            for(int row=0;row<height;row++)
            {
                value=(image.getRGB(col,row)&0xffffff)>>16;

             /*   int r = (value>>16)&0xff;
                int g = (value>>8)&0xff;
                int b = value&0xff;
                int avg = (r+g+b)/3;
*/

                matrix[col][row]=value;
                counter++;
            }
        }

    }

    public String constructImageWithSpllitedMatrix(ArrayList<ArrayList<Integer>>imageBlocks) {

        int i=0,col=0,row=0;
        BufferedImage constructImage ;
        constructImage=new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int k = 0; k < imageBlocks.size();k++) {
          //  System.out.println("Size  "+imageBlocks.get(k).size());

            for (int x = 0; x < blockSize; x++) {

                for (int y = 0; y < blockSize; y++) {
                    constructImage.setRGB(col, row, (imageBlocks.get(k).get(i))+(imageBlocks.get(k).get(i)<<8)+(imageBlocks.get(k).get(i)<<16));
                    i++;
                    row++;
                }
                if(x!=blockSize-1) {
                    row = row - blockSize;

                }
                col++;
            }
            i=0;
            if(row==image.getHeight()) {
                row = 0;
              //  System.out.println("enter");
            }
            else
            {
                col=col-blockSize;
               // System.out.println("enter2");
            }
        }
        File file = new File("F:\\FCI\\3rd YEAR\\testing\\outputImage.png");
        try {
            ImageIO.write(constructImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "F:\\FCI\\3rd YEAR\\testing\\outputImage.png";

    }
    public ArrayList<ArrayList<Integer>> generateCodeBookPixels(ArrayList<codeBlock>codeBlock)
    {   ArrayList<ArrayList<Integer>>blocks=new ArrayList<>();
        ArrayList<Integer>block;
        for(int i=0;i<codeBlock.size();i++)
        {
            block=new ArrayList<>();
            block=codeBlock.get(i).getPixels();
            blocks.add(block);
        }
        return blocks;
    }
    public  String constructImage()
    {
        int width=image.getWidth();
        int height=image.getHeight();
        BufferedImage constructedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<width;i++)
        {
            for(int j=0;j<height;j++)
            {
                constructedImage.setRGB(i,j,matrix[i][j]);
            }
        }
        File file=new File("E:\\java\\OutputImage.png");
        try {
            ImageIO.write(constructedImage,"png",file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "E:\\java\\OutputImage.png";
    }

    public int calculateDistance(ArrayList<Integer>source,ArrayList<Integer>distention)
    {
        int distance=0;
        for(int i=0;i<source.size();i++)
        {
            distance+=(int)pow(source.get(i)-distention.get(i),2);
           // System.out.println(" i "+ pow(source.get(i)-distention.get(i),2));
        }
        return distance;
    }
    public ArrayList<Integer> calculateAvg(ArrayList<ArrayList<Integer>>samples)
    {
        ArrayList<Integer> avg = new ArrayList<>();
        for(int i=0;i<samples.get(0).size();i++)
        {
            int sum=0;
            for (ArrayList <Integer> sample : samples) {
                sum += sample.get(i);
            }
            sum/=samples.size();
            avg.add(sum);
        }
        return  avg;

    }
    public void printMatrix()
    {
        for(int i=0;i<image.getWidth();i++)
        {
            for(int j=0;j<image.getHeight();j++)
            {
                System.out.println("pixel "+  matrix[i][j]);
            }
        }
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getCodeBookSize() {
        return codeBookSize;
    }

    public void setCodeBookSize(int codeBookSize) {
        this.codeBookSize = codeBookSize;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCodeBook(ArrayList<codeBlock> codeBook) {
        this.codeBook = codeBook;
    }

    public void setSpittedMatrix(ArrayList<ArrayList<Integer>> spittedMatrix) {
        this.spittedMatrix = spittedMatrix;
    }
    public ArrayList<codeBlock> getCodeBook()
    {
        return codeBook;
    }
}
