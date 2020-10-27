package pazuru.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.logging.Logger;

public class FileUtil {
    static String savingPath = "";
    static String savingExt = ".png";

    public static void setSavingPath(String path) {
        savingPath = path;
    }

    public static void saveToLocal(BufferedImage img, String filename) throws IOException {
        if (savingPath.equals("")) throw new IOException();
        File outputfile = new File(savingPath, filename + savingExt);
        ImageIO.write(img, ext2type(savingExt), outputfile);
        Logger.getLogger("pazuru").fine("saved " + filename + savingExt);
    }

    public static void saveToLocal(List<PazuruImg> images, PazuruMenu list) throws IOException {
        if (images.size() != list.size()) throw new IOException();
        for (int i = 0; i < images.size(); i++) {
            saveToLocal(images.get(i).getImg(), list.getGroup(i));
        }
    }

    public static void saveToLocal(InputStream is, String filename) throws IOException {
        saveToLocal(ImageIO.read(is), filename);
    }

    public static String ext2type(String ext) {
        if (ext.toLowerCase().equals(".png")) return "png";
        if (ext.toLowerCase().equals(".jpg") || ext.toLowerCase().equals(".jpeg")) return "jpg";
        if (ext.toLowerCase().equals(".gif")) return "gif";
        return "png";
    }

    public static String indexFileName(int index, int total) {
        return String.format("%0" + String.valueOf(total + 1).length() + "d", index + 1);
    }
}
