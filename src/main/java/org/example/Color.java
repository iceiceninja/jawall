package org.example;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor

public class Color {
    // looked into making these shorts instead of ints, but it doesn't seem like it really improves anything
    // since it seems that java stores bytes/shorts in the same sized 32-bit cell that it stores an int
    // only real performance enchancment would be if you had an array of shorts/bytes because then it seems
    // it would fit 4 shorts/bytes in a 32-bit cell
    private int alpha = 255;
    private int red;
    private int green;
    private int blue;

    public Color(String hex)
    {
       //Give support for alpha
        red = Integer.parseInt(hex.substring(1,3), 16);
        green = Integer.parseInt(hex.substring(3,5), 16);
        blue = Integer.parseInt(hex.substring(5,7), 16);
    }

    double getColorDist(Color color)
    {
        double distance = 0;
        int alphaDist = getAbsDist(this.getAlpha(),color.getAlpha());
        int redDist = getAbsDist(this.getRed(),color.getRed());
        int greenDist = getAbsDist(this.getGreen(),color.getGreen());
        int blueDist = getAbsDist(this.getBlue(),color.getBlue());

        distance = Math.sqrt(Math.pow(alphaDist,2)+
                Math.pow(redDist,2)+
                Math.pow(greenDist,2)+
                Math.pow(blueDist,2));
        return distance;
    }
    double getColorDist(int alpha, int red, int green, int blue)
    {
        double distance;
        int alphaDist = getAbsDist(this.getAlpha(),alpha);
        int redDist = getAbsDist(this.getRed(),red);
        int greenDist = getAbsDist(this.getGreen(),green);
        int blueDist = getAbsDist(this.getBlue(),blue);

        distance = Math.sqrt(Math.pow(alphaDist,2)+
                Math.pow(redDist,2)+
                Math.pow(greenDist,2)+
                Math.pow(blueDist,2));
        return distance;
    }
    private int getAbsDist(int num1, int num2)
    {
        if(num1>num2)
        {
            return num1-num2;
        }
        return num2-num1;
    }
    public int getPixelInt()
    {
        int pixelColor = 0;
        pixelColor |= alpha<<24;
        pixelColor |= red<<16;
        pixelColor |= green<<8;
        pixelColor |= blue;

        return pixelColor;
    }
}
