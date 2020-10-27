/*
 * This solver is greatly inspired by EnkanRec's work
 */
package pazuru.site.comic_fuz;

import pazuru.util.PazuruKey;

public class PuzzleMap {

    public static PazuruKey getPuzzleMap(int width, int height, int block_width, int block_height, int key) {
        int n_blocks_x = width / block_width;
        int n_blocks_y = height / block_height;
        int short_block_width = width % block_width;
        int short_block_height = height % block_height;
        PazuruKey ret = new PazuruKey();

        int short_block_x_pos = n_blocks_x - ((43 * key) % n_blocks_x);
        if (short_block_x_pos % n_blocks_x == 0) {
            short_block_x_pos = (n_blocks_x - 4) % n_blocks_x;
        }
        if (short_block_x_pos == 0) {
            short_block_x_pos = n_blocks_x - 1;
        }
        int short_block_y_pos = n_blocks_y - ((47 * key) % n_blocks_y);
        if (short_block_y_pos % n_blocks_y == 0) {
            short_block_y_pos = (n_blocks_y - 4) % n_blocks_y;
        }
        if (short_block_y_pos == 0) {
            short_block_y_pos = n_blocks_y - 1;
        }

        if (short_block_width > 0 && short_block_height > 0) {
            int src_x_px = short_block_x_pos * block_width;
            int src_y_px = short_block_y_pos * block_height;
            ret.add(src_y_px, src_x_px, src_y_px, src_x_px, short_block_height, short_block_width);
        }

        if (short_block_height > 0) {
            for (int _x_pos = 0; _x_pos < n_blocks_x; _x_pos++) {
                int shifted_x_pos = calcXCoordinateXRest_(_x_pos, n_blocks_x, key);
                int dst_y_pos = calcYCoordinateXRest_(shifted_x_pos, short_block_x_pos, short_block_y_pos, n_blocks_y, key);
                int dst_x_px = calcPositionWithRest_(shifted_x_pos, short_block_x_pos, short_block_width, block_width);
                int dst_y_px = dst_y_pos * block_height;
                int src_x_px = calcPositionWithRest_(_x_pos, short_block_x_pos, short_block_width, block_width);
                int src_y_px = short_block_y_pos * block_height;
                ret.add(dst_y_px, dst_x_px, src_y_px, src_x_px, short_block_height, block_width);
            }
        }

        if (short_block_width > 0) {
            for (int _y_pos = 0; _y_pos < n_blocks_y; _y_pos++) {
                int shifted_y_pos = calcYCoordinateYRest_(_y_pos, n_blocks_y, key);
                int dst_x_pos = calcXCoordinateYRest_(shifted_y_pos, short_block_x_pos, short_block_y_pos, n_blocks_x, key);
                int dst_x_px = dst_x_pos * block_width;
                int dst_y_px = calcPositionWithRest_(shifted_y_pos, short_block_y_pos, short_block_height, block_height);
                int src_x_px = short_block_x_pos * block_width;
                int src_y_px = calcPositionWithRest_(_y_pos, short_block_y_pos, short_block_height, block_height);
                ret.add(dst_y_px, dst_x_px, src_y_px, src_x_px, block_height, short_block_width);
            }
        }
        for (int _x_pos = 0; _x_pos < n_blocks_x; _x_pos++) {
            for (int _y_pos = 0; _y_pos < n_blocks_y; _y_pos++) {
                int shifted_x_pos = (_x_pos + 29 * key + 31 * _y_pos) % n_blocks_x;
                int shifted_y_pos = (_y_pos + 37 * key + 41 *
                        shifted_x_pos) % n_blocks_y;
                int dst_x_px = shifted_x_pos * block_width + (shifted_x_pos >= calcXCoordinateYRest_(
                        shifted_y_pos, short_block_x_pos, short_block_y_pos, n_blocks_x, key)? short_block_width : 0);
                int dst_y_px = shifted_y_pos * block_height + (shifted_y_pos >= calcYCoordinateXRest_(
                        shifted_x_pos, short_block_x_pos, short_block_y_pos, n_blocks_y, key)? short_block_height : 0);
                int src_x_px = _x_pos * block_width + (_x_pos >= short_block_x_pos? short_block_width : 0);
                int src_y_px = _y_pos * block_height + (_y_pos >= short_block_y_pos? short_block_height : 0);
                ret.add(dst_y_px, dst_x_px, src_y_px, src_x_px, block_height, block_width);
            }
        }

        return ret;
    }

    static int calcPositionWithRest_(int e, int t, int r, int i) {
        return e * i + (e >= t ? r : 0);
    }


    static int calcXCoordinateXRest_(int e, int t, int r) {
        return (e + 61 * r) % t;
    }

    static int calcYCoordinateXRest_(int shifted_x_pos, int short_block_x_pos, int short_block_y_pos, int n_blocks_y, int key) {
        boolean o = (key % 2) == 1;
        int a, s;
        if (shifted_x_pos < short_block_x_pos ? o : !o) {
            a = short_block_y_pos;
            s = 0;
        } else {
            a = n_blocks_y - short_block_y_pos;
            s = short_block_y_pos;
        }
        return (shifted_x_pos + 53 * key + 59 * short_block_y_pos) % a + s;
    }



    static int calcXCoordinateYRest_(int shifted_y_pos, int short_block_x_pos, int short_block_y_pos, int n_blocks_x, int key) {
        boolean o = (key % 2) == 1;
        int a, s;
        if (shifted_y_pos < short_block_y_pos? o : !o) {
            a = n_blocks_x - short_block_x_pos;
            s = short_block_x_pos;
        } else {
            a = short_block_x_pos;
            s = 0;
        }
        return (shifted_y_pos + 67 * key + short_block_x_pos + 71) % a + s;
    }


    static int calcYCoordinateYRest_(int e, int t, int r) {
        return (e + 73 * r) % t;
    }

    public static int calculateKey(String page_path) {
        String file_str = page_path + "/0";
        int n = 0;
        for (int c : file_str.toCharArray()) {
            n += c;
        }
        return n % 4 + 1;
    }

}
