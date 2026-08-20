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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import tw.master.GlobalsClientGui;
import tw.master.brain.Brain;
import tw.master.crawler.AbstractBrainCrawler;
import tw.master.crawler.Crawler;
import tw.master.crawler.CrawlerList;
import tw.master.crawler.CrawlerListUpdateInterface;
import tw.master.engine.EngineEventsInterface;
import tw.master.utils.Utils;



@SuppressWarnings("serial")
public class CrawlerTablePanel extends JPanel implements CrawlerListUpdateInterface, EngineEventsInterface {

    private int[]            colSizes = { 80, 80, 150, 90, 100, 90, 90, 90, 50 };

    private GlobalsClientGui g;

    private CrawlerList      crawlers;

    private MyTableModel     myTableModel;

    private JTable           bestJTable;

    private JScrollPane      scrollPane;

    class MyTableModel extends DefaultTableModel {

        @Override
        public int getRowCount() {
            return crawlers.size();
        }

        @Override
        public int getColumnCount() {
            return colSizes.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "step";
                case 1:
                    return "gen";
                case 2:
                    return "live";
                case 3:
                    return "fitness";
                case 4:
                    return "complexity";
                case 5:
                    return "visit";
                case 6:
                    return "energy";
                case 7:
                    return "energyChange";
                case 8:
                    return "status";
                default:
                    break;
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 8) { return true; }
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            synchronized (crawlers) {
                double f = crawlers.getValue(rowIndex);
                if (Double.isNaN(f)) { return "xxx"; }
                Crawler c = crawlers.getCrawler(rowIndex);
                if (c == null) return null;

                Brain brain;

                if (c instanceof AbstractBrainCrawler) brain = ((AbstractBrainCrawler) c).getBrain();
                else
                    brain = null;

                switch (columnIndex) {
                    case 0:
                        return Long.toString(c.getStepCount());
                    case 1:
                        return c.getName();
                    case 2:
                        return c.liveState;

                    case 3: {
                        float le = c.getFitness();
                        return String.format("%+3.2f", new Float(le));
                    }

                    case 4: {
                        if (brain != null) {
                            return Long.toString((long) (brain.getBrainComplexity() + 0.5f));
                        } else {
                            return null;
                        }
                    }

                    case 5: {
                        float le = c.getVisitEnergy();
                        return String.format("%+5.2f", new Float(le));
                    }
                    case 6: {
                        return Long.toString((long) (c.getEnergy() + 0.5));
//						if (brain != null) {
//							return Long.toString((long) (brain.getBrainEnergyUsed()+0.5f));
//						}
//						else {
//							return null;
//						}
                    }
                    case 7:
                        float le = c.getLastEnergyChanges();
                        return String.format("%+5.1f", new Float(le));

                    case 8:
                        return "Show";
                        // return new Boolean(true);
                    default:
                        break;

                }
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            // We don't change values :-)
            // super.setValueAt(aValue, row, column);
        }


    }

    class ColorCellRenderer extends DefaultTableCellRenderer {

        Color[] ca = {
                new Color(255, 255, 255),
                new Color(233, 233, 233)
        };

        @Override
        public Component getTableCellRendererComponent(JTable ltable, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(ltable, value, isSelected, hasFocus, row, column);

            if (!isSelected && !hasFocus) setBackground(ca[row % ca.length]);

            Crawler c = crawlers.getCrawler(row);

            if (c != null && g.engine.watchedCrawler == c) setBackground(Color.ORANGE);

            return this;
        }
    }

// ---------------------------------------------------------------------------------------------

    public CrawlerTablePanel(CrawlerList crawlers, String name, GlobalsClientGui g) {
        setName(name);

        this.crawlers = crawlers;
        crawlers.registerCrawlerListListener(this);

        this.g = g;

        myTableModel = new MyTableModel();
        bestJTable = new JTable(myTableModel);

        bestJTable.setDefaultRenderer(Object.class, new ColorCellRenderer());
        bestJTable.setShowGrid(true);
        bestJTable.setGridColor(Color.GRAY);

        // table.setEditingColumn(5);
        bestJTable.getColumn("status").setCellRenderer(new ButtonRenderer());
        bestJTable.getColumn("status").setCellEditor(new ButtonEditor(new JCheckBox()));

        bestJTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bestJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                Crawler c = CrawlerTablePanel.this.crawlers.getCrawler(bestJTable.getSelectedRow());
                CrawlerTablePanel.this.g.engine.setWatchedCrawler(c);
            }

        });
//		table.setAutoCreateRowSorter(true);

        JTableHeader header = bestJTable.getTableHeader();
//		header.setForeground(Color.yellow);
//		header.setBackground(Color.black);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        // Disable auto resizing
        bestJTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set the columns wide
        int i = 0;
        for (int w : colSizes) {
            TableColumn col = bestJTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(w);
            i++;
        }
        scrollPane = new JScrollPane(bestJTable);
        bestJTable.setFillsViewportHeight(true);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        new TableUpdateThread().start();

        g.engine.addChangeListener(this);

    }

    class TableUpdateThread extends Thread {

        TableUpdateThread() {
            setName("TableUpdateThread");
        }

        @Override
        public void run() {
            while (true) {
                if (crawlers.updateCounter > 0) {

                    Utils.repaintCtrl(CrawlerTablePanel.this, 0);

                    if (crawlers.updateCounter > 1) {
                        // System.err.println("Co " + updateCounter);
                        crawlers.updateCounter = 1;
                    } else
                        crawlers.updateCounter = 0;
                }
                if (Utils.isFrontmostApplication()) Utils.sleep(500);
                else
                    Utils.sleep(2000);
            }
        }
    }

    public CrawlerList getBestList() {
        return crawlers;
    }

    public void setBestList(CrawlerList newBestCrawlers) {
        crawlers = newBestCrawlers;
        scrollPane.revalidate();
        // System.err.println("NB");
        Utils.repaintCtrl(this, 0);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        protected JButton button;

        private String    label;

        private boolean   isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setEnabled(true);
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //System.out.println(label + ": " + e);

                    Crawler c = crawlers.getCrawler(bestJTable.getSelectedRow());

                    g.openCrawlerWindow(c);

                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = value == null ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                System.out.println(label + ": Ouch : " + CrawlerTablePanel.this.bestJTable.getEditingRow());
            }
            isPushed = false;
            return new String(label);
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
            // return true;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    @Override
    public void updateData() {

    }

    @Override
    public void sizeChanged() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                scrollPane.revalidate();
            }
        });
    }

    /* (non-Javadoc)
     * @see tw.master.engine.EngineEventsInterface#serverEvent(tw.master.engine.EngineEventsInterface.ServerEvent, tw.master.crawler.Crawler)
     */
    @Override
    public void serverEvent(ServerEvent se, Crawler c) {
        if (se == ServerEvent.WatchedChanged)
            Utils.repaintCtrl(CrawlerTablePanel.this, 0);
    }

}
