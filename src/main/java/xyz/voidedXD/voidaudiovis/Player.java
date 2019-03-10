package xyz.voidedXD.voidaudiovis;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class Player extends JPanel implements ActionListener {
    private Minim minim;
    private AudioPlayer song;
    private FFT fft;

    public Graphics graphics;

    private int duration;
    //private JLabel time;
    private String durationHuman;

//    private int[] oldibands;

    public Player(File f) {
        minim = new Minim(this);
        song = minim.loadFile(f.getAbsolutePath(), 2048);
        fft = new FFT(song.bufferSize(), song.sampleRate());
        duration = song.length();
        durationHuman = toHR(duration);
        //time = new JLabel();
        //time.setForeground(Color.cyan);
        //this.add(time);
        this.setOpaque(false);
        song.play();
    }

    public InputStream createInput(String fileName) {
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return is;
    }
    public String sketchPath(String fileName) {
        return fileName;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.graphics = g;
        g.setColor(Color.white);
        if (song.isPlaying()) {
            int[] is = new int[fft.specSize()];
            float[] bands = new float[fft.specSize()];
            int[] ibands = new int[fft.specSize()];
            fft.forward(song.mix);

            for (int i = 0; i < bands.length; i++) {
                bands[i] = this.getHeight() - fft.getBand(i);
                ibands[i] = (int)bands[i];
                is[i] = i;
            }

            int compress = 512;
            int[] cibands = new int[compress];
            int[] cis = new int[compress];
            for(int i = 0; i < compress; i++) {
                int fac = is.length / compress;
                for(int a = 0; a < fac; a++) {
                    cibands[i] = ibands[(i*fac)+a];
                }
                cis[i] = i;
            }

            ibands = cibands;
            is = cis;

            for(int a = 0; a < 5; a++) {
                for (int i = 0; i < is.length; i++) {
                    if(i != is.length - 1) {
                        ibands[i] = (int) (ibands[i + 1] * 0.9f + ibands[i] * (1-0.9f));
                    }
                    if (ibands[i] == ibands[0]) {
                        ibands[0] = (ibands[0] + ibands[1]) / 2;
                    } else if (i == is.length - 1) {
                        ibands[i] = (ibands[i - 1] + ibands[i]) / 2;
                    } else {
                        ibands[i] = (ibands[i - 1] + ibands[i] + ibands[i + 1]) / 3;
                    }
                }
            }
            vis(graphics, cis, cibands);
            repaint();
            updateUI();
//            oldibands = ibands;
            //time.setText(toHR(song.position()) + " / " + durationHuman);
        } else {
            song.close();
            minim.stop();
        }
    }

    public void vis(Graphics g, int[] i, int[] band) {
        i[i.length - 2] = i.length;
        i[i.length - 1] = 0;
        band[band.length - 2] = this.getHeight();
        band[band.length - 1] = this.getHeight();
        g.fillPolygon(i, band, i.length);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void removeNotify() {
        song.close();
        minim.stop();
    }

    public String toHR(int millis) {
        return String.format("%d:%02d:%03d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                millis % 1000
        );
    }
}
