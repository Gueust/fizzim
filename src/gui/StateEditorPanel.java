package gui;

/*
 Copyright 2007 Zimmer Design Services.
 Written by Michael Zimmer - mike@zimmerdesignservices.com
 Copyright 2014 Jean-Baptiste Lespiau jeanbaptiste.lespiau@gmail.com

 This file is part of Fizzim.

 Fizzim is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 Fizzim is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

import locale.UTF8Control;
import attributes.ObjAttribute;
import display.DrawArea;
import display.StatePropertiesPanel;
import entities.StateObj;

/**
 * The edge editor panel is composed of two tabs:
 * - the first one allows to modify the value of attributes
 * - the second one allows to modify all the fields of all attributes and the
 * color of the edge
 */
@SuppressWarnings("serial")
public class StateEditorPanel extends JPanel {
  private static final ResourceBundle locale =
      ResourceBundle.getBundle("locale.Editors", new UTF8Control());

  private JTabbedPane tabbedPane;
  private JTextArea[] fields;
  private StatePropertiesPanel second_tab;
  private StateObj state;

  /*
   * This constant values correspond to the position of the tab in the
   * JTabbedPane. Tabs are numbered beginning at 0.
   */
  private static final int GENERAL_TAB = 0;
  private static final int DETAILS_TAB = 1;

  /**
   * Create the panel that is used in the editor of the states
   * 
   * @param window
   *          The parent window used to position opened windows and to retrieve
   *          the ok and cancel bouton
   * @param draw_area
   *          The drawing area to update after modifications
   * @param state
   *          The state being edited
   */
  public StateEditorPanel(GeneralEditorWindow window, DrawArea draw_area,
      StateObj state) {
    this.state = state;

    setBorder(new EmptyBorder(7, 7, 7, 7));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    add(tabbedPane);

    JComponent first_tab = new JPanel();
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 47, 334 };
    gridBagLayout.rowHeights = new int[] { 19, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    first_tab.setLayout(gridBagLayout);

    LinkedList<ObjAttribute> attributes = state.getAttributeList();
    int nb_attributes = attributes.size();
    fields = new JTextArea[nb_attributes];
    int y_index = 0;
    for (ObjAttribute one_attribute : attributes) {
      String name = (String) one_attribute.get(0);
      String value = (String) one_attribute.get(1);

      JLabel lblEvents = new JLabel(name + ":");
      GridBagConstraints gbc_lblEvents = new GridBagConstraints();
      gbc_lblEvents.anchor = GridBagConstraints.NORTHWEST;
      gbc_lblEvents.insets = new Insets(0, 0, 5, 0);
      gbc_lblEvents.gridx = 0;
      gbc_lblEvents.gridy = y_index;
      first_tab.add(lblEvents, gbc_lblEvents);

      /* Text Area */
      JTextArea events_textArea = new JTextArea(value);
      events_textArea.setLineWrap(true);
      events_textArea
          .setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
      /*
       * Unlike TransitionEditorPanel, we have a different case for the name
       * attribute
       */
      if (name.equals("name")) {
        events_textArea.setRows(1);
        events_textArea.setColumns(30);
        GridBagConstraints gbc_events_textArea = new GridBagConstraints();
        gbc_events_textArea.anchor = GridBagConstraints.NORTHWEST;
        gbc_events_textArea.insets = new Insets(0, 0, 5, 0);
        gbc_events_textArea.gridx = 1;
        gbc_events_textArea.gridy = y_index;
        gbc_events_textArea.fill = GridBagConstraints.NONE;
        gbc_events_textArea.weightx = 0;
        gbc_events_textArea.weighty = 0;

        first_tab.add(events_textArea, gbc_events_textArea);
      } else {
        events_textArea.setRows(3);
        events_textArea.setColumns(30);
        GridBagConstraints gbc_events_textArea = new GridBagConstraints();
        gbc_events_textArea.anchor = GridBagConstraints.NORTHWEST;
        gbc_events_textArea.insets = new Insets(0, 0, 5, 0);
        gbc_events_textArea.gridx = 1;
        gbc_events_textArea.gridy = y_index;
        gbc_events_textArea.fill = GridBagConstraints.BOTH;
        gbc_events_textArea.weightx = 1;
        gbc_events_textArea.weighty = 1;

        first_tab.add(events_textArea, gbc_events_textArea);
      }

      fields[y_index] = events_textArea;
      y_index++;
    }

    tabbedPane.addTab(locale.getString("general_tab"), null, first_tab,
        locale.getString("general_tab_description"));

    second_tab = new StatePropertiesPanel(window,
        draw_area, state);

    tabbedPane.addTab(locale.getString("details_tab"), null, second_tab,
        locale.getString("details_tab_description"));

    tabbedPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateData(tabbedPane.getSelectedIndex());
      }
    });

    /*
     * We add the update and cancel actions to the OK and Cancel button of the
     * parent window
     */
    window.getBtnOk().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        /* We update the data of the details tab with the data in General */
        updateData(DETAILS_TAB);
        second_tab.SPOKActionPerformed(evt);
      }
    });

    window.getBtnCancel().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        second_tab.SPCancelActionPerformed(evt);
      }
    });
  }

  /*
   * This updates the given tab with the data of other tabs.
   * 
   * @details This is used when changing tabs
   */
  public void updateData(int tab_selected) {
    if (tab_selected == GENERAL_TAB) { // We select the general tab
      /* We force the commit the current cell of tab 2 */
      TableCellEditor cell_editor = second_tab.getTable().getCellEditor();
      if (cell_editor != null)
        cell_editor.stopCellEditing();
      /* We get the value of the fields and update the first tab */
      LinkedList<ObjAttribute> attributes = state.getAttributeList();
      int y_index = 0;
      for (ObjAttribute one_attribute : attributes) {
        String value = (String) one_attribute.get(1);
        fields[y_index].setText(value);

        y_index++;
      }

    } else if (tab_selected == DETAILS_TAB) { // We select the details tab
      /* We update the value of the name in the JTable */
      LinkedList<ObjAttribute> attributes = state.getAttributeList();
      int y_index = 0;
      for (ObjAttribute one_attribute : attributes) {
        String value = fields[y_index].getText();
        one_attribute.set(1, value);
        /* This ugly line is necessary since name is duplicated within states... */
        if (one_attribute.getName().equals("name")) {
          state.setName(value);
        }
        /* This line is necessary to consider the new value added as local. */
        one_attribute.setEditable(1, ObjAttribute.LOCAL);

        y_index++;
      }
      // second_tab.getTable().getModel().setValueAt(value, y_index, 1);
    }
  }
}
