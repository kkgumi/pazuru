package pazuru.util;

import pazuru.exception.UnexpectedKeyException;

public class RegularPazuruKey extends PazuruKey {
    int imgRowSize, imgColSize, row, col, rowSize, colSize;

    public RegularPazuruKey(int imgRowSize, int imgColSize, int row, int col, int rowSize, int colSize) throws UnexpectedKeyException {
        this.imgRowSize = imgRowSize;
        this.imgColSize = imgColSize;
        this.row = row;
        this.col = col;
        this.rowSize = rowSize;
        this.colSize = colSize;
        if (row * rowSize > imgRowSize || col * colSize > imgColSize) throw new UnexpectedKeyException();
        if (row * rowSize < imgRowSize && col * colSize < imgColSize) {
            super.entities.add(new PazuruKeyEntity(row*rowSize, col*colSize, row*rowSize, col*colSize,
                    imgRowSize-row*rowSize, imgColSize-col*colSize));
        }
        if (row * rowSize < imgRowSize) {
            super.entities.add(new PazuruKeyEntity(row*rowSize, 0, row*rowSize, 0,
                    imgRowSize-row*rowSize, col*colSize));
        }
        if (col * colSize < imgColSize) {
            super.entities.add(new PazuruKeyEntity(0, col*colSize, 0, col*colSize,
                    row*rowSize, imgColSize-col*colSize));
        }
    }

    public void set(int[] key) throws UnexpectedKeyException {
        if (key.length != row*col) throw new UnexpectedKeyException();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                int src = key[i*row + j];
                if (src < 0 || src >= row*col) throw new UnexpectedKeyException();
                super.entities.add(new PazuruKeyEntity(i * rowSize, j * colSize,
                        src/row * rowSize, (src%row) * colSize, rowSize, colSize));
            }
        }
    }
}
