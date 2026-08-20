/*
 *  This file is part of the EvolutionBrain project.
 *
 *  Copyright (c) 2011 by Thomas Welsch (ttww@gmx.de). All rights reserved.
 *
 *  EvolutionBrain is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  EvolutionBrain is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with EvolutionBrain.  If not, see <http://www.gnu.org/licenses/>.
 */

package tw.master.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class DisplayedCounter {

    private long             count;

    private long             actCount;

    private transient JLabel titleLabel;

    private transient JLabel outLabel1;

    private transient JLabel outLabel2;

    private transient String format1;

    private transient String format2;

    private long             div = 1;

    static transient Font    textFont;

    static transient Font    numberFont;

    public DisplayedCounter() { }

    public void setupGui(JPanel panel, String title, String format1) {
        setupGui(panel, title, format1, null);
    }

    public void setupGui(JPanel panel, String title, String format1, String format2) {
        if (textFont == null) {
            textFont = new JLabel().getFont().deriveFont(Font.BOLD);
            numberFont = new Font("Courier", 0, new JLabel().getFont().getSize() + 1);
        }

        this.titleLabel = new JLabel(title);
        this.outLabel1 = new JLabel();
        this.outLabel2 = new JLabel();
        this.format1 = format1;
        this.format2 = format2;

        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(Color.LIGHT_GRAY);
        titleLabel.setFont(textFont);

        outLabel1.setFont(numberFont);
        outLabel1.setHorizontalAlignment(SwingConstants.LEFT);
        outLabel1.setOpaque(true);
        outLabel1.setBackground(Color.LIGHT_GRAY);

        outLabel2.setHorizontalAlignment(SwingConstants.LEFT);
        outLabel2.setFont(numberFont);

        panel.add(titleLabel);

        panel.add(outLabel1);
        panel.add(outLabel2);
    }


    public void add() {
        count++;
        actCount++;
    }

    public void add(int n) {
        count += n;
        actCount += n;
    }

    public void add(long n) {
        count += n;
        actCount += n;
    }

    public void setDivisor(long div) {
        this.div = div;
    }

    private long lastCPSValue;

    private long lastUpdatedMs;

    private long lastDisplayedValue = -27513951;

    private long lastDisplayedCountValue;

    public void updateDisplay() {
        long now = System.currentTimeMillis();

        long used = now - lastUpdatedMs;
        //if (used < 200) return;

//		long countsPerSec = (long) ((actCount * 1000f) / used);
        long countsPerSec = (long) (actCount * 1000f / used * 0.7f + lastCPSValue * 0.3f);

        lastCPSValue = countsPerSec;

        actCount = 0;
        lastUpdatedMs = now;

        //System.err.println(String.format(format,new Long(count/div),new Long(countsPerSec/div)));

        if (lastDisplayedCountValue != count || lastDisplayedValue != countsPerSec) {
            lastDisplayedCountValue = count;
            lastDisplayedValue = countsPerSec;
            if (format1 != null) {
                outLabel1.setText(String.format(format1, new Long(count / div)));
            }
            if (format2 != null) {
                outLabel2.setText(String.format(format2, new Long(countsPerSec / div)));
            }
        }
    }

    public long getCounter() {
        return count;
    }

    public void setCounter(long newCount) {
        count = newCount;
    }

}
