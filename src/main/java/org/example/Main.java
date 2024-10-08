package org.example;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        //take in image (or images/directory) and get the RGB of the pixels
        //For now use hard coded CherryBlossom.jpg

        //Also load and parse config.yml to get the themes as well as colors
        // Parse config.yml and return a list of Color objects for colors of the chosen theme
        // then pass those colors to

        ArgParser argParser = new ArgParser();
        ParsedInput input = argParser.parse(args);

        String userHome = System.getProperty("user.home");
        ConfigManager configManager = new ConfigManager(Paths.get(userHome, ".config", "jawall", "config.yml").toString()); // Hard coded config file spot "~/.config/jawall/config.yml"

        // Get correct theme based on command line args

        List<Color> themeColors = configManager.getThemeSet()
                .getThemes()
                .stream()
                .filter(t -> t.getName().equals(input.getThemeName()))//iceiceninja2
                .findFirst()
                .map(theme -> theme.getColors().stream()
                        .map(Color::new)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>()); // Handle the case when the theme is not found


        // FOR IMAGE PREVIEWING, UNCOMMENT ANY CODE IN THIS METHOD WITH frame
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().setLayout(new FlowLayout());

        try
        {
            BufferedImage inputImage = ImageIO.read(new File(input.getFilepath()));//"./src/main/resources/wallpaper1.jpg"
            BufferedImage outputImage = getImageFromArray(createThemedImage(inputImage, themeColors),inputImage.getWidth(),inputImage.getHeight());
            Path filepath = Paths.get(input.getFilepath());
            if(!filepath.isAbsolute())
                filepath = filepath.toAbsolutePath();
            saveImageToFile(outputImage, filepath.getParent() +File.separator +"jawall_" + filepath.getFileName());
//            frame.getContentPane().add(new JLabel(new ImageIcon(outputImage)));
//            frame.setPreferredSize(new Dimension(outputImage.getWidth(),outputImage.getHeight()));
        }catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

//        frame.pack();
//        frame.setVisible(true);

    }
    // Should return array of int
    private static int[] createThemedImage(BufferedImage image,List<Color> themeColors) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;
        final int pixelLength = image.getColorModel().getPixelSize()/8; // getPixelSize returns how many bits are in a pixel so we divide by 8 to get bytes

        int[] result = new int[height*width];
        if (hasAlphaChannel) {
            for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
//                int argb = 0;
//                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
//                argb += ((int) pixels[pixel + 1] & 0xff); // blue
//                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
//                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red

                int red= (int) pixels[pixel + 3] & 0xff;
                int green = (int) pixels[pixel+2] & 0xff;
                int blue = (int) pixels[pixel+1] & 0xff;
                int alpha = (int) pixels[pixel] & 0xff;
                result[(row * width) + col] = getClosestColor(alpha,red ,green,blue,themeColors).getPixelInt();;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
//                argb += -16777216; // 255 alpha. Commented out because it feels like a magic number

                //Here we are constructing an int whose binary form contains the BGR values we need
                /*
                    0xff in binary is 11111111 (or 8 ones).
                    lets start from the beginning
                    argb = 00000000000000000000000000000000
                    argb is an int, which is made up of 4 bytes. Each byte is 8 bits and a bit is a one or a zero.
                    when we add 0xff << 24 to argb from the start we are telling our program to shift 0xff 24 bits to the left then add it (or OR it)
                    For example:
                    argb : 00000000000000000000000000000000                 00000000000000000000000000000000
                          +11111111000000000000000000000000               | 11111111000000000000000000000000
                          _________________________________                 _________________________________
                          11111111000000000000000000000000                  11111111000000000000000000000000

                   In this case we do this because there is no alpha channel so we make the pixel opaque
                   11111111 in binary equals 255 in decimal if you were unaware.

                   So if you were trying to set a pixel to the value of argb(255,32,130,255) then you would
                   a =  11111111 (this is 255)
                   r =  00010000 (this is 32)
                   g =  01000010 (this is 130)
                   b =  11111111 (this is also 255 (I got too lazy to do a diff number)

                   then you shift them over by multiples of 8 based on where you want them in the int and then do a bitwise OR '|' operation
                   to change the bits

                   int pixelColor = 00000000000000000000000000000000
                   pixelColor |= a<<24
                   pixelColor is now  11111111000000000000000000000000
                   pixelColor |= r<<16
                   pixelColor is now  11111111000100000000000000000000
                   pixelColor |= g<<8
                   pixelColor is now  11111111000100000100001000000000
                   pixelColor |= b  (no need to shift since we are just targeting the first byte)
                   pixelColor is now  11111111000100000100001011111111

                   then to get the ARGB values from this int you do the following

                   red = 0
                   red |= pixelColor << 24
                   short green = (short) ((argb & (0xff << 8)));
                   this shifts the byte of 1's 8 bits before bitwise ANDing with our final int

                            ALPHA     RED       GREEN      BLUE
                    argb = 11111111  01101011  01000001  01010101
                    0xff =                               11111111

                    Then shift 0xff 8 bits
                            ALPHA     RED       GREEN      BLUE
                    argb = 11111111  01101011  01000001  01010101
                    0xff =                     11111111 <--------

                    Then perform bitwise AND & operation and store result

                            ALPHA     RED       GREEN      BLUE
                    argb = 11111111  01101011  01000001  01010101
                    0xff =                   & 11111111 <--------
                                             ------------
                                               01000001
                    short green = 65 (or 01000001 in binary)

                 */
//                int argb = 0;
//                argb |= 0xff << 24;
//
//                // REMINDER: Pixels is an array of bytes. Each byte is a value (Alpha, Red, Green, or Blue)
//                // When we go pixels[pixel+1] we arent actually going to the next pixel.
//                argb |= (((int) pixels[pixel + 2] & 0xff) << 16); // red
//                argb |= (((int) pixels[pixel + 1] & 0xff) << 8); // green
//                argb |= ((int) pixels[pixel] & 0xff); // blue
//                short alpha=0;
//                alpha |= (short) ((argb & (0xff<< 8)));
//                String test0 = Integer.toBinaryString(alpha);
//                String test3 = Integer.toBinaryString(argb);
//                String test1 = Integer.toBinaryString(((int) pixels[pixel+1] & 0xff) <<8);

                int red= (int) pixels[pixel + 2] & 0xff;
                int green = (int) pixels[pixel+1] & 0xff;//pixels[pixel + 1];
                int blue = (int) pixels[pixel] & 0xff;
                result[row*width + col] = getClosestColor(255,red ,green,blue,themeColors).getPixelInt();//argb; // change result to be the output image after color change?

                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }
    static Color getClosestColor(int alpha, int red, int green, int blue, List<Color> themeColors)
    {
        //Compare color that gets passed in with list of template colors. Closest template color
        // Probably make sure theme colors isnt empty
        int minDist = 999999999;
        Color closestColor = new Color("#000000");
        for(Color color : themeColors)
        {
            double colorDist = color.getColorDist(alpha,red,green,blue);
            if(colorDist < minDist)
            {
                minDist = (int) colorDist;
                closestColor = color;
            }
        }
        if(minDist == 999999999)
        {
            System.out.println("ERROR: Could not find closest color. Default to black #000000");
        }
        return  closestColor;
    }
    public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
//        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        WritableRaster raster = (WritableRaster) image.getData();
//        raster.setPixels(0,0,width,height,pixels);
//        return image;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }
    public static void saveImageToFile(BufferedImage image, String filename) {
        // Convert Image to BufferedImage
//        BufferedImage bufferedImage = (BufferedImage) image;

        // Save the image to a file
        try {
            File outputfile = new File(filename);
            ImageIO.write(image, "png", outputfile);
            System.out.println("Image saved successfully to: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save the image: " + e.getMessage());
        }
    }
}