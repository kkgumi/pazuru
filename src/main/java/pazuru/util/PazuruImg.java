package pazuru.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class PazuruImg {
    private BufferedImage originalImg, img;
    private PazuruKey key = null;

    public PazuruImg(InputStream is) throws IOException {
        this.originalImg = ImageIO.read(is);
        this.img = this.originalImg;
    }

    public void setKey(PazuruKey key) {
        this.key = key;
        solve();
    }

    public void corp(int width, int height) {
        this.img = this.img.getSubimage(0, 0, width, height);
    }

    private void solve() {
        if (this.key == null) return;
        this.img = new BufferedImage(this.originalImg.getWidth(), this.originalImg.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graph = this.img.createGraphics();
        Color oldColor = graph.getColor();
        graph.setPaint(Color.WHITE);
        graph.fillRect(0, 0, this.originalImg.getWidth(), this.originalImg.getHeight());
        graph.setColor(oldColor);
        for (PazuruKey.PazuruKeyEntity entity : this.key.pazuruKeyList()) {
            BufferedImage piece = originalImg.getSubimage(entity.srcCol, entity.srcRow, entity.colSize, entity.rowSize);
            graph.drawImage(piece, null, entity.dstCol, entity.dstRow);
        }
        graph.dispose();
        Logger.getLogger("pazuru").finest("solve");
    }

    public BufferedImage getImg() {
        return this.img;
    }

    public int getWidth() {
        return this.img.getWidth();
    }

    public int getHeight() {
        return this.img.getHeight();
    }
}
