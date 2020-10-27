package pazuru.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PazuruKey {
    public static class PazuruKeyEntity {
        int srcRow;
        int srcCol;
        int dstRow;
        int dstCol;
        int rowSize;
        int colSize;

        PazuruKeyEntity(int srcRow, int srcCol, int dstRow, int dstCol, int rowSize, int colSize) {
            this.srcRow = srcRow;
            this.srcCol = srcCol;
            this.dstRow = dstRow;
            this.dstCol = dstCol;
            this.rowSize = rowSize;
            this.colSize = colSize;
        }
    }

    List<PazuruKeyEntity> entities = new ArrayList<>();

    public PazuruKey() {
    }

    public void add(int srcRow, int srcCol, int dstRow, int dstCol, int rowSize, int colSize) {
        this.entities.add(new PazuruKeyEntity(srcRow, srcCol, dstRow, dstCol, rowSize, colSize));
        Logger.getLogger("pazuru").finest("add key " + String.valueOf(srcRow) + ", " + String.valueOf(srcCol));
    }

    public List<PazuruKeyEntity> pazuruKeyList() {
        return this.entities;
    }
}
