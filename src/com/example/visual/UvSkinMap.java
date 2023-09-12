package com.example.visual;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class UvSkinMap extends JPanel {

    private Image img;
    private Image subSprite;

    private final int offsetX = 66;
    private final int offsetY = 50;

    //Skin coords ---------head_front-----head_layer-----chest_front-------jacket----------right_leg-------left_leg---------right_arm---------left_arm
    int[][] skinCoords = {{8, 8, 8, 8}, {40, 8, 8, 8}, {20, 20, 8, 12},{20, 37, 8, 11}, {4, 20, 4, 12}, {20, 52, 4, 12}, {44, 20, 4, 12}, {36, 52, 4, 12}};

    public UvSkinMap(Image img) {
        if (img != null) {
            this.img = img;
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setLayout(null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.img != null) {
            //this.img = this.img.getScaledInstance(128, 128, Image.SCALE_DEFAULT);
            BufferedImage bimage = toBufferedImage(this.img);

            if (bimage.getHeight() == 32) {

                //Head
                subSprite = bimage.getSubimage(skinCoords[0][0], skinCoords[0][1], skinCoords[0][2], skinCoords[0][3]);
                int SCALE_NUMBER = 8;
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, offsetY, skinCoords[0][2] * SCALE_NUMBER, skinCoords[0][3] * SCALE_NUMBER, null);

                //chest
                subSprite = bimage.getSubimage(20, 20, 8, 12);
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, 8 * SCALE_NUMBER + offsetY, 8 * SCALE_NUMBER, 12 * SCALE_NUMBER, null);

                //right arm
                subSprite = bimage.getSubimage(44, 20, 4, 12);
                g.drawImage(subSprite, offsetX, 8 * SCALE_NUMBER + offsetY, 4 * SCALE_NUMBER, 12 * SCALE_NUMBER, null);

                //left arm
                subSprite = bimage.getSubimage(48, 20, 8, 12);
                g.drawImage(subSprite, 12 * SCALE_NUMBER + offsetX, 8 * SCALE_NUMBER + offsetY, 4 * SCALE_NUMBER, 12 * SCALE_NUMBER, null);

                //legs
                subSprite = bimage.getSubimage(4, 20, 4, 12);
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, 20 * SCALE_NUMBER + offsetY, 4 * SCALE_NUMBER, 12 * SCALE_NUMBER, null);
                g.drawImage(subSprite, 8 * SCALE_NUMBER + offsetX, 20 * SCALE_NUMBER + offsetY, 4 * SCALE_NUMBER, 12 * SCALE_NUMBER, null);


            } else {
                //Head
                subSprite = bimage.getSubimage(skinCoords[0][0], skinCoords[0][1], skinCoords[0][2], skinCoords[0][3]);
                int SCALE_NUMBER = 8;
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, offsetY, skinCoords[0][2] * SCALE_NUMBER, skinCoords[0][3] * SCALE_NUMBER, null);

                //Head_layer
                subSprite = bimage.getSubimage(skinCoords[1][0], skinCoords[1][1], skinCoords[1][2], skinCoords[1][3]);
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, offsetY, skinCoords[1][2] * SCALE_NUMBER, skinCoords[1][3] * SCALE_NUMBER, null);

                //chest_front
                subSprite = bimage.getSubimage(skinCoords[2][0], skinCoords[2][1], skinCoords[2][2], skinCoords[2][3]);
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, 8 * SCALE_NUMBER + offsetY, skinCoords[2][2] * SCALE_NUMBER, skinCoords[2][3] * SCALE_NUMBER, null);

                //jacket
                subSprite = bimage.getSubimage(skinCoords[3][0], skinCoords[3][1], skinCoords[3][2], skinCoords[3][3]);
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, 8 * SCALE_NUMBER + offsetY, skinCoords[3][2] * SCALE_NUMBER, skinCoords[3][3] * SCALE_NUMBER, null);

                //right_leg
                subSprite = bimage.getSubimage(skinCoords[4][0], skinCoords[4][1], skinCoords[4][2], skinCoords[4][3]);
                g.drawImage(subSprite, 4 * SCALE_NUMBER + offsetX, 20 * SCALE_NUMBER + offsetY, skinCoords[4][2] * SCALE_NUMBER, skinCoords[4][3] * SCALE_NUMBER, null);

                //left_leg
                subSprite = bimage.getSubimage(skinCoords[5][0], skinCoords[5][1], skinCoords[5][2], skinCoords[5][3]);
                g.drawImage(subSprite, 8 * SCALE_NUMBER + offsetX, 20 * SCALE_NUMBER + offsetY, skinCoords[5][2] * SCALE_NUMBER, skinCoords[5][3] * SCALE_NUMBER, null);

                //right_arm
                subSprite = bimage.getSubimage(skinCoords[6][0], skinCoords[6][1], skinCoords[6][2], skinCoords[6][3]);
                g.drawImage(subSprite, offsetX, 8 * SCALE_NUMBER + offsetY, skinCoords[6][2] * SCALE_NUMBER, skinCoords[6][3] * SCALE_NUMBER, null);

                //left_arm
                subSprite = bimage.getSubimage(skinCoords[7][0], skinCoords[7][1], skinCoords[7][2], skinCoords[7][3]);
                g.drawImage(subSprite, 12 * SCALE_NUMBER + offsetX, 8 * SCALE_NUMBER + offsetY, skinCoords[7][2] * SCALE_NUMBER, skinCoords[7][3] * SCALE_NUMBER, null);

            }
            g.dispose();
        }
    }

    public void setImage(Image img) {
        this.img = img;
    }

    public Image getImage() {
        return this.img;
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
