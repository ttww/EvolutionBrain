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

package tw.master.remote;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import tw.gui.ZebraJTable;
import tw.master.engine.Engine;
import tw.master.utils.UpdateListenerInterface;

public class AvailableServersPanel extends JPanel implements UpdateListenerInterface {

    private int[]             colSizes         = { 100, 40, 150, 40, 120, 100, 200 };

    private static final long serialVersionUID = 1L;

//	JmDnsHelper			api;
    RemoteEnginesManager      remoteEnginesManager;

    List<RemoteEngine>        remoteEngines;

    RemoteEngineTableModel    stm;

    public AvailableServersPanel(Engine engine) {
        super(new BorderLayout());
//		System.err.println("AAAAAAAAAAAAAAA");
//		api = new JmDnsHelper("_javaobj._tcp.local.");
//		services = api.getServices();
//		api.startRefreshingServices();
//		System.err.println("BBBBBBBBBBBB");


        remoteEnginesManager = engine.remoteEngines;
        engine.remoteEngines.registerUpdateListener(this);

        stm = new RemoteEngineTableModel();
        remoteEngines = remoteEnginesManager.getRemoteEngines();

//		TableUpdateThread t = new TableUpdateThread();
//		t.setDaemon(true);
//		t.setName("Bonjour collector thread");
//		t.start();


        JTable table = new ZebraJTable(stm);
        table.setFillsViewportHeight(true);

        // Disable auto resizing
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set the columns wide
        int i = 0;
        for (int w : colSizes) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(w);
            i++;
        }

//		table.getColumn("Run").setCellRenderer(table.getDefaultRenderer(Boolean.class));
//		table.getColumn("Run").setCellEditor(  table.getDefaultEditor(Boolean.class));

        JScrollPane scrollPane = new JScrollPane(table);

        this.add(scrollPane, BorderLayout.CENTER);

    }

    class RemoteEngineTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        String[]                  columnNames      = {
                "Name",
                "Run",
                "URL",
                "CPUs",
                "Memory",
                "Status",
                "Message",
        };

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 1) return Boolean.class;
            return super.getColumnClass(columnIndex);
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return remoteEngines.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            RemoteEngine re;
            try {
                re = remoteEngines.get(row);
            } catch (IndexOutOfBoundsException e) {
                // Temp. this is ok :-)
                return "";
            }
            switch (col) {
                case 0:
                    return re.remoteName;
                case 1:
                    return re.remoteIsRunning ? Boolean.TRUE : Boolean.FALSE; // ? "yes" : "no";
                case 2:
                    return re.service.getURL();
                case 3:
                    return new Integer(re.remoteCPUs);
                case 4:
                    return String.format("%5d MB (%5d MB)", new Long(re.remoteFreeMem), new Long(re.remoteMaxMem));
                case 5:
                    return re.state;
                case 6:
                    return re.message;
                default:
                    break;
            }
            return "???";
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (col == 1) return true;
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            RemoteEngine re;
            try {
                re = remoteEngines.get(row);

                //System.err.println("set Value "+value+"  at "+col + ","+ row);

                re.setRemoteRunning(((Boolean) value).booleanValue());

            } catch (IndexOutOfBoundsException e) {
                // Temp. this is ok :-)
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void doUpdate() {
//		System.err.println("DDDD");
        remoteEngines = remoteEnginesManager.getRemoteEngines();

//		for (RemoteEngine re : remoteEngines) {
//			System.err.println("RE = "+re.remoteName);
//		}
        stm.fireTableDataChanged();
    }


}
