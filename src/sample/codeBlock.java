package sample;

import java.util.ArrayList;

public class codeBlock {
    private String code;
    private ArrayList<Integer>pixels;

    public codeBlock(ArrayList<Integer>pixels)
    {   this.pixels=new ArrayList<>();
        this.pixels.addAll(pixels);
        code="";
    }

    public codeBlock() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Integer> getPixels() {
        return pixels;
    }

    public void setPixels(ArrayList<Integer> pixels) {
        this.pixels = pixels;
    }
}
