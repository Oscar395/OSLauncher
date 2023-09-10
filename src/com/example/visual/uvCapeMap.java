package com.example.visual;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class uvCapeMap extends JPanel {

    private Image img;
    private Image subsSprite;

    private int x;
    private int y;

    public uvCapeMap(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.img != null) {
            BufferedImage bufferedImage = UvSkinMap.toBufferedImage(this.img);

            this.subsSprite = bufferedImage.getSubimage(1, 0, this.x, this.y);
            int SCALE_NUMBER = 8;
            g.drawImage(this.subsSprite, 0, 0, 11 * SCALE_NUMBER, 17 * SCALE_NUMBER, null);
        }
    }

    public void setImg(Image img) {
        this.img = img;
    }
}
