package com.example.visual;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class uvCapeMap extends JPanel {

    private Image img;
    private Image subsSprite;

    private int x;
    private int y;

    private int offsetX;
    private int offsetY;

    private int SCALE_NUMBER;

    public uvCapeMap(int x, int y, int offsetX, int offsetY, int SCALE_NUMBER) {
        this.x = x;
        this.y = y;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.SCALE_NUMBER = SCALE_NUMBER;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.img != null) {
            try {
                BufferedImage bufferedImage = UvSkinMap.toBufferedImage(this.img);

                this.subsSprite = bufferedImage.getSubimage(this.offsetX, this.offsetY, this.x, this.y);
                g.drawImage(this.subsSprite, 0, 0, this.x * this.SCALE_NUMBER, this.y * this.SCALE_NUMBER, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setImg(Image img) {
        this.img = img;
    }
}
