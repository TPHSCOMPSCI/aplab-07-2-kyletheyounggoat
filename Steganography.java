import java.awt.*;
import java.util.ArrayList;
public class Steganography {
    public static void clearLow( Pixel p ){
        p.setRed(p.getRed()/4*4) ;
        p.setBlue(p.getBlue()/4*4) ;
        p.setGreen(p.getGreen()/4*4) ;
    }


    public static Picture testClearLow(Picture pic) {
        Picture copy = new Picture(pic);
        Pixel[][] pixels = copy.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel p : row) {
                clearLow(p);
            }
        }
        return copy;
    }


    public static void setLow(Pixel p, Color c) {
        clearLow(p); 

        int red = p.getRed() + (c.getRed() / 64);     
        int green = p.getGreen() + (c.getGreen() / 64);
        int blue = p.getBlue() + (c.getBlue() / 64);

        p.setColor(new Color(red, green, blue));
    }

    public static Picture testSetLow(Picture pic, Color c) {
        Picture copy = new Picture(pic);
        Pixel[][] pixels = copy.getPixels2D();
        for (Pixel[] row : pixels) {
            for (Pixel p : row) {
                setLow(p, c);
            }
        }
        return copy;
    }

    public static Picture revealPicture(Picture hidden) {
        Picture copy = new Picture(hidden);
        Pixel[][] source = hidden.getPixels2D();
        Pixel[][] pixels = copy.getPixels2D();

        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                Color col = source[r][c].getColor();

                int red = (col.getRed() % 4) * 64;   
                int green = (col.getGreen() % 4) * 64;
                int blue = (col.getBlue() % 4) * 64;

                pixels[r][c].setColor(new Color(red, green, blue));
            }
        }

        return copy;
    }
      public static boolean canHide(Picture source, Picture secret) {
        return source.getWidth() == secret.getWidth() &&
               source.getHeight() == secret.getHeight();
    }
    public static Picture hidePicture(Picture source, Picture secret) {
        Picture combined = new Picture(source);
        Pixel[][] sourcePixels = combined.getPixels2D();
        Pixel[][] secretPixels = secret.getPixels2D();

        for (int r = 0; r < sourcePixels.length; r++) {
            for (int c = 0; c < sourcePixels[0].length; c++) {
                setLow(sourcePixels[r][c], secretPixels[r][c].getColor());
            }
        }

        return combined;
    }
    public static Picture hidePicture(Picture source, Picture secret, int startRow, int startColumn) {
        Picture combined = new Picture(source);
        Pixel[][] sourcePixels = combined.getPixels2D();
        Pixel[][] secretPixels = secret.getPixels2D();
    
        for (int r = 0; r < secretPixels.length; r++) {
            for (int c = 0; c < secretPixels[0].length; c++) {
                if (startRow + r < sourcePixels.length && startColumn + c < sourcePixels[0].length) {
                    setLow(sourcePixels[startRow + r][startColumn + c], secretPixels[r][c].getColor());
                }
            }
        }
    
        return combined;
    }
    public static boolean isSame(Picture pic1, Picture pic2) {
        if (pic1.getWidth() != pic2.getWidth() || pic1.getHeight() != pic2.getHeight()) {
            return false;
        }
    
        Pixel[][] pixels1 = pic1.getPixels2D();
        Pixel[][] pixels2 = pic2.getPixels2D();
    
        for (int r = 0; r < pixels1.length; r++) {
            for (int c = 0; c < pixels1[0].length; c++) {
                if (!pixels1[r][c].getColor().equals(pixels2[r][c].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }
    public static ArrayList<Point> findDifferences(Picture pic1, Picture pic2) {
        ArrayList<Point> differences = new ArrayList<>();

        if (pic1.getWidth() != pic2.getWidth() || pic1.getHeight() != pic2.getHeight()) {
            return differences;
        }

        Pixel[][] pixels1 = pic1.getPixels2D();
        Pixel[][] pixels2 = pic2.getPixels2D();

        for (int r = 0; r < pixels1.length; r++) {
            for (int c = 0; c < pixels1[0].length; c++) {
                if (!pixels1[r][c].getColor().equals(pixels2[r][c].getColor())) {
                    differences.add(new Point(c, r));
                }
            }
        }

        return differences;
    }
    public static Picture showDifferentArea(Picture original, ArrayList<Point> points) {
        Picture marked = new Picture(original);
        if (points.size() == 0) return marked;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Point pt : points) {
            int x = (int) pt.getX();
            int y = (int) pt.getY();
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
        }

        Pixel[][] pixels = marked.getPixels2D();
        Color borderColor = Color.RED;

        for (int c = minX; c <= maxX; c++) {
            if (minY >= 0 && minY < pixels.length) pixels[minY][c].setColor(borderColor);
            if (maxY >= 0 && maxY < pixels.length) pixels[maxY][c].setColor(borderColor);
        }

        for (int r = minY; r <= maxY; r++) {
            if (minX >= 0 && minX < pixels[0].length) pixels[r][minX].setColor(borderColor);
            if (maxX >= 0 && maxX < pixels[0].length) pixels[r][maxX].setColor(borderColor);
        }

        return marked;
    }  
    public static ArrayList<Integer> encodeString(String s) {
        s = s.toUpperCase();
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < s.length(); i++) {
            if (s.substring(i,i+1).equals(" ")) {
                result.add(27);
            }
            else {
                result.add(alpha.indexOf(s.substring(i,i+1))+1);
            }
        }
        result.add(0);
        return result;
    }
    public static String decodeString(ArrayList<Integer> codes) {
        String result = "";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i=0; i < codes.size(); i++) {
            if (codes.get(i) == 27) {
                result = result + " ";
            }
            else {
                result = result +
                    alpha.substring(codes.get(i)-1,codes.get(i));
            }
        }
        return result;
    }
 
    private static int[] getBitPairs(int num) {
        int[] bits = new int[3];
        int code = num;
        for (int i = 0; i < 3; i++) {
            bits[i] = code % 4;
            code = code / 4;
        }
        return bits;
    }
    public static void hideText(Picture source, String s) {
        ArrayList<Integer> codes = encodeString(s);
        Pixel[][] pixels = source.getPixels2D();
        int index = 0;
        for (int r = 0; r < pixels.length && index < codes.size(); r++) {
            for (int c = 0; c < pixels[0].length && index < codes.size(); c++) {
                int[] bits = getBitPairs(codes.get(index));
                pixels[r][c].setRed((pixels[r][c].getRed() / 4 * 4) + bits[0]);
                pixels[r][c].setGreen((pixels[r][c].getGreen() / 4 * 4) + bits[1]);
                pixels[r][c].setBlue((pixels[r][c].getBlue() / 4 * 4) + bits[2]);
                index++;
            }
        }
    }
    public static String revealText(Picture source) {
        Pixel[][] pixels = source.getPixels2D();
        ArrayList<Integer> codes = new ArrayList<>();
        for (int r = 0; r < pixels.length; r++) {
            for (int c = 0; c < pixels[0].length; c++) {
                int red = pixels[r][c].getRed() % 4;
                int green = pixels[r][c].getGreen() % 4;
                int blue = pixels[r][c].getBlue() % 4;
                int num = blue * 16 + green * 4 + red;
                if (num == 0) {
                    return decodeString(codes);
                }
                codes.add(num);
            }
        }
        return decodeString(codes);
    }
 

    public static void main(String[] args) {
        Picture beach = new Picture("beach.jpg");
        beach.explore();

        Picture copy = testClearLow(beach);
        copy.explore();
        Picture motorcycle = new Picture("blueMotorcycle.jpg");
        Picture hidden = hidePicture(beach, motorcycle);
        hidden.explore();

        Picture arch = new Picture("arch.jpg");
        if (canHide(beach, arch)) {
            Picture combined = hidePicture(beach, arch);
            combined.explore();

            Picture revealed = revealPicture(combined);
            revealed.explore();
        } else {
            System.out.println("Cannot hide: images are not the same dimensions.");
        }

        Picture robot = new Picture("robot.jpg");
        Picture flower1 = new Picture("flower1.jpg");
        beach.explore();
        Picture hidden1 = hidePicture(beach, robot, 65, 208);
        Picture hidden2 = hidePicture(hidden1, flower1, 280, 110);
        hidden2.explore();
        Picture unhidden = revealPicture(hidden2);
        unhidden.explore(); 

        Picture swan = new Picture("swan.jpg");
        Picture swan2 = new Picture("swan.jpg");
        System.out.println("Swan and swan2 are the same: " +
        isSame(swan, swan2));
        swan = testClearLow(swan);
        System.out.println("Swan and swan2 are the same (after clearLow run on swan): "
        + isSame(swan, swan2));



        Picture koala = new Picture("koala.jpg") ;
        Picture robot1 = new Picture("robot.jpg");
        Picture arch2 = hidePicture(arch, robot1, 65, 102);
        ArrayList<Point> pointList = findDifferences(arch, arch2);
        System.out.println("PointList after comparing two identical s pictures " + "has a size of " + pointList.size());
        pointList = findDifferences(arch, koala);
        System.out.println("PointList after comparing two different sized pictur t es " + "has a size of " + pointList.size());
        pointList = findDifferences(arch, arch2);
        System.out.println("Pointlist after hiding a picture has a siz m e of" + pointList.size());
        arch.show();
        arch2.show(); 
       
        Picture hall = new Picture("femaleLionAndHall.jpg");
        Picture robot2 = new Picture("robot.jpg");
        Picture flower2 = new Picture("flower1.jpg");
        Picture hall2 = hidePicture(hall, robot2, 50, 300);
        Picture hall3 = hidePicture(hall2, flower2, 115, 275);
        hall3.explore();
        if(!isSame(hall, hall3)) {
        Picture hall4 = showDifferentArea(hall,
            findDifferences(hall, hall3));
        hall4.show();
        Picture unhiddenHall3 = revealPicture(hall3);
        unhiddenHall3.show();
        }
        String hiddenMessage = "WHY HELLO THERE";
        System.out.println("Hidden message: " + hiddenMessage);
        hideText(beach, hiddenMessage);
        beach.explore();
        String revealedMessage = revealText(beach);
        System.out.println("Revealed message: " + revealedMessage);

    }

}

