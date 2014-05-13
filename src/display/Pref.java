package display;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;

//Written by: Michael Zimmer - mike@zimmerdesignservices.com

/*
 Copyright 2007 Zimmer Design Services

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

@SuppressWarnings("serial")
public class Pref extends javax.swing.JDialog {

  DrawArea drawArea;
  JColorChooser colorChooser;
  Component window = this;

  Color tempSC;
  Color tempSTC;
  Color tempLTC;

  /** Creates new form Pref */
  public Pref(java.awt.Frame parent, boolean modal, DrawArea da) {
    super(parent, modal);
    drawArea = da;
    tempSC = drawArea.getDefSC();
    tempSTC = drawArea.getDefSTC();
    tempLTC = drawArea.getDefLTC();
    this.setTitle("Preferences");
    colorChooser = drawArea.getColorChooser();
    initComponents();
  }

  private Color getColor(String type)
  {
    if (type.equals("SC"))
      return tempSC;
    else if (type.equals("STC"))
      return tempSTC;
    else if (type.equals("LTC"))
      return tempLTC;
    else
      return Color.black;
  }

  private void setColor(String type, Color c)
  {
    if (type.equals("SC"))
      tempSC = c;
    if (type.equals("STC"))
      tempSTC = c;
    if (type.equals("LTC"))
      tempLTC = c;
  }

  private void setUpColorBox(JLabel jLabel, String type)
  {
    jLabel.setPreferredSize(new Dimension(50, 20));
    jLabel.setMinimumSize(new Dimension(50, 20));
    jLabel.setOpaque(true);
    jLabel.setVisible(true);
    jLabel.setBackground(getColor(type));
    jLabel.setBorder(new LineBorder(Color.black, 1));
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // GEN-BEGIN:initComponents
  // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
  private void initComponents() {
    globalTablePanel = new javax.swing.JPanel();
    tableVisCheckbox = new javax.swing.JCheckBox();
    jLabel1 = new javax.swing.JLabel();
    spaceTextField = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    fontComboBox = new javax.swing.JComboBox();
    fontSizeComboBox = new javax.swing.JComboBox();
    fontColorButton = new javax.swing.JButton();
    jPanel2 = new javax.swing.JPanel();
    jLabel5 = new javax.swing.JLabel();
    font2ComboBox = new javax.swing.JComboBox();
    font2SizeComboBox = new javax.swing.JComboBox();
    gridCheckbox = new javax.swing.JCheckBox();
    jLabel7 = new javax.swing.JLabel();
    gridSTextField = new javax.swing.JTextField();
    lineWidthTextField = new javax.swing.JTextField();
    jLabel11 = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    jLabel10 = new javax.swing.JLabel();
    jButton1 = new javax.swing.JButton();
    jButton2 = new javax.swing.JButton();
    SC1 = new javax.swing.JLabel();
    SC2 = new javax.swing.JLabel();
    STC1 = new javax.swing.JLabel();
    STC2 = new javax.swing.JLabel();
    LTC1 = new javax.swing.JLabel();
    LTC2 = new javax.swing.JLabel();
    SPW = new javax.swing.JLabel();
    SPH = new javax.swing.JLabel();
    SPWField = new javax.swing.JFormattedTextField(NumberFormat
        .getIntegerInstance());
    SPHField = new javax.swing.JFormattedTextField(NumberFormat
        .getIntegerInstance());

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    globalTablePanel.setBorder(javax.swing.BorderFactory
        .createTitledBorder("Global Table"));
    tableVisCheckbox.setSelected(drawArea.getTableVis());
    tableVisCheckbox.setText("Table Visible");
    tableVisCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(
        0, 0, 0, 0));
    tableVisCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

    jLabel1.setText("Space between columns:");

    spaceTextField.setText(String.valueOf(drawArea.getSpace()));

    jLabel2.setText("pixels");

    jLabel3.setText("Table Font:");

    // default state size
    SPW.setText("Default State Width:");
    SPH.setText("Default State Height:");
    SPWField.setValue(new Integer(drawArea.getStateW()));
    SPWField.setColumns(4);
    SPHField.setValue(new Integer(drawArea.getStateH()));
    SPHField.setColumns(4);

    // default colors
    SC1.setText("Default State Color:");
    STC1.setText("Default State Transition Color:");
    LTC1.setText("Default Loopback Transition Color:");

    setUpColorBox(SC2, "SC");
    setUpColorBox(STC2, "STC");
    setUpColorBox(LTC2, "LTC");

    SC2.addMouseListener(new MouseListener() {

      // JColorChooser colorChooser = new JColorChooser();
      ActionListener colorSel = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          SC2.setBackground(colorChooser.getColor());
          setColor("SC", colorChooser.getColor());
        }
      };

      public void mouseClicked(MouseEvent e)
      {
        JDialog dialog;
        dialog = JColorChooser.createDialog(window, "Choose Color", true,
            colorChooser, colorSel, null);
        dialog.setVisible(true);
      }

      public void mouseEntered(MouseEvent e) {
      }

      public void mouseExited(MouseEvent e) {
      }

      public void mousePressed(MouseEvent e) {
      }

      public void mouseReleased(MouseEvent e) {
      }
    });

    STC2.addMouseListener(new MouseListener() {

      // JColorChooser colorChooser = new JColorChooser();
      ActionListener colorSel = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          STC2.setBackground(colorChooser.getColor());
          setColor("STC", colorChooser.getColor());
        }
      };

      public void mouseClicked(MouseEvent e)
      {
        JDialog dialog;
        dialog = JColorChooser.createDialog(window, "Choose Color", true,
            colorChooser, colorSel, null);
        dialog.setVisible(true);
      }

      public void mouseEntered(MouseEvent e) {
      }

      public void mouseExited(MouseEvent e) {
      }

      public void mousePressed(MouseEvent e) {
      }

      public void mouseReleased(MouseEvent e) {
      }
    });

    LTC2.addMouseListener(new MouseListener() {

      // JColorChooser colorChooser = new JColorChooser();
      ActionListener colorSel = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          LTC2.setBackground(colorChooser.getColor());
          setColor("LTC", colorChooser.getColor());
        }
      };

      public void mouseClicked(MouseEvent e)
      {
        JDialog dialog;
        dialog = JColorChooser.createDialog(window, "Choose Color", true,
            colorChooser, colorSel, null);
        dialog.setVisible(true);
      }

      public void mouseEntered(MouseEvent e) {
      }

      public void mouseExited(MouseEvent e) {
      }

      public void mousePressed(MouseEvent e) {
      }

      public void mouseReleased(MouseEvent e) {
      }
    });

    // get list of fonts
    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String[] fontNames = env.getAvailableFontFamilyNames();

    fontComboBox.setModel(new javax.swing.DefaultComboBoxModel(fontNames));
    fontComboBox.setSelectedItem(drawArea.getTableFont().getFontName());

    fontSizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(
        new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "20",
            "22", "24", "30", "36", "42", "48", "72" }));
    fontSizeComboBox.setSelectedItem(String.valueOf(drawArea
        .getTableFont()
        .getSize()));

    fontColorButton.setBackground(drawArea.getTableColor());
    // fontColorButton.setAlignmentX(0.5F);
    // fontColorButton.setOpaque(true);
    fontColorButton.setPreferredSize(new java.awt.Dimension(20, 20));
    fontColorButton.setBorder(new LineBorder(Color.black, 1));
    fontColorButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fontColorButtonActionPerformed(evt);
      }
    });

    GroupLayout globalTablePanelLayout = new GroupLayout(globalTablePanel);
    globalTablePanel.setLayout(globalTablePanelLayout);
    globalTablePanelLayout
        .setHorizontalGroup(globalTablePanelLayout
            .createParallelGroup(Alignment.LEADING)
            .addGroup(globalTablePanelLayout
                .createSequentialGroup()
                .addContainerGap()
                .addGroup(globalTablePanelLayout
                    .createParallelGroup(Alignment.LEADING)
                    .addComponent(tableVisCheckbox)
                    .addGroup(globalTablePanelLayout
                        .createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(spaceTextField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addGroup(globalTablePanelLayout
                        .createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(
                            ComponentPlacement.RELATED)
                        .addComponent(fontComboBox,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(fontSizeComboBox,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(
                            ComponentPlacement.RELATED)
                        .addComponent(fontColorButton,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(136, Short.MAX_VALUE)));
    globalTablePanelLayout.setVerticalGroup(globalTablePanelLayout
        .createParallelGroup(Alignment.LEADING)
        .addGroup(globalTablePanelLayout
            .createSequentialGroup()
            .addComponent(tableVisCheckbox)
            .addPreferredGap(
                ComponentPlacement.RELATED)
            .addGroup(globalTablePanelLayout
                .createParallelGroup(Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(spaceTextField,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2))
            .addPreferredGap(ComponentPlacement.RELATED)
            .addGroup(globalTablePanelLayout
                .createParallelGroup(Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(fontComboBox,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addComponent(fontSizeComboBox,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE)
                .addComponent(fontColorButton,
                    GroupLayout.PREFERRED_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.PREFERRED_SIZE))
            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    jPanel2.setBorder(javax.swing.BorderFactory
        .createTitledBorder("Draw Area"));
    jLabel5.setText("Font:");

    font2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(fontNames));
    font2ComboBox.setSelectedItem(drawArea.getFont().getFontName());
    font2SizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(
        new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "20",
            "22", "24", "30", "36", "42", "48", "72" }));
    font2SizeComboBox.setSelectedItem(String.valueOf(drawArea
        .getFont()
        .getSize()));

    gridCheckbox.setSelected(drawArea.getGrid());
    gridCheckbox.setText("Grid");
    gridCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        0, 0, 0));
    gridCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));

    jLabel7.setText("Grid Size:");

    gridSTextField.setText(String.valueOf(drawArea.getGridSpace()));

    jLabel11.setText("Line Width:");
    jLabel12.setText("pixels");

    lineWidthTextField.setText(String.valueOf(drawArea.getLineWidth()));
    lineWidthTextField.setColumns(2);

    // jLabel7.setText("Grid Size:");

    // gridSTextField.setText(String.valueOf(drawArea.getGridSpace()));

    jLabel8.setText("pixels");
    jLabel9.setText("pixels");
    jLabel10.setText("pixels");
    GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(jPanel2Layout.
        createParallelGroup(Alignment.LEADING).addGroup(jPanel2Layout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
                .addGroup(jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(jLabel5)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(font2ComboBox,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(font2SizeComboBox,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE))
                .addComponent(gridCheckbox)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jLabel7)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(gridSTextField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(jLabel8))
                .addGroup(jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(jLabel11)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lineWidthTextField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(jLabel12))
                .addGroup(jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(SPW)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(SPWField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(jLabel9))
                .addGroup(jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(SPH)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(SPHField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(jLabel10))
                .addGroup(jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(SC1)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(SC2))
                .addGroup(jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(STC1)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(STC2))
                .addGroup(jPanel2Layout
                    .createSequentialGroup()
                    .addComponent(LTC1)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(LTC2)))
            .addContainerGap(208, Short.MAX_VALUE)));

    jPanel2Layout.setVerticalGroup(jPanel2Layout
        .createParallelGroup(Alignment.LEADING)
        .addGroup(
            jPanel2Layout
                .createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(font2ComboBox,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(font2SizeComboBox,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(gridCheckbox)
                .addPreferredGap(ComponentPlacement.RELATED,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(gridSTextField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(lineWidthTextField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(ComponentPlacement.RELATED,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(SPW)
                    .addComponent(SPWField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(ComponentPlacement.RELATED,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(SPH)
                    .addComponent(SPHField,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(ComponentPlacement.RELATED,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(SC1)
                    .addComponent(SC2))
                .addPreferredGap(ComponentPlacement.RELATED,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(STC1)
                    .addComponent(STC2))
                .addPreferredGap(ComponentPlacement.RELATED,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(LTC1)
                    .addComponent(LTC2))
                .addContainerGap()));

    jButton1.setText("Cancel");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    jButton2.setText("OK");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
        .addGroup(layout
            .createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addComponent(jPanel2,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addComponent(globalTablePanel,
                    GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)
                .addGroup(Alignment.TRAILING,
                    layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
            .addContainerGap()));
    layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(globalTablePanel,
                GroupLayout.PREFERRED_SIZE,
                GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED)
            .addComponent(jPanel2,
                GroupLayout.PREFERRED_SIZE,
                GroupLayout.DEFAULT_SIZE,
                GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                .addComponent(jButton1).addComponent(jButton2))
            .addContainerGap()));
    pack();
  }// </editor-fold>//GEN-END:initComponents

  ActionListener okListener = new ActionListener() {
    // Called when user clicks ok
    public void actionPerformed(ActionEvent evt) {
      // Note: The source is an internal button in the dialog
      // and should not be used

      // Get selected color
      fontColorButton.setBackground(colorChooser.getColor());
      // fontColorButton.
    }
  };

  protected void fontColorButtonActionPerformed(ActionEvent evt) {

    // colorChooser = new JColorChooser();
    colorChooser.setColor(fontColorButton.getBackground());
    // colorChooser.setVisible(true);
    JDialog dialog = JColorChooser.createDialog(fontColorButton,
        "Pick a Color",
        true,  // modal
        colorChooser,
        okListener,  // OK button handler
        null); // no CANCEL button handler

    dialog.setVisible(true);

    // Make the renderer reappear.
    // fireEditingStopped();

  }

  // GEN-FIRST:event_jButton1ActionPerformed
  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
    dispose();
  }// GEN-LAST:event_jButton1ActionPerformed

  // GEN-FIRST:event_jButton2ActionPerformed
  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {

    drawArea.setTableVis(tableVisCheckbox.isSelected());
    try {
      drawArea.setSpace(Integer.parseInt(spaceTextField.getText()));
    } catch (NumberFormatException nfe) {
      drawArea.setSpace(20);
    }
    drawArea.setTableFont(new Font((String) fontComboBox.getSelectedItem(),
        Font.PLAIN, Integer.parseInt((String) fontSizeComboBox
            .getSelectedItem())));

    drawArea.setTableColor(fontColorButton.getBackground());

    drawArea.setFont(new Font((String) font2ComboBox.getSelectedItem(),
        Font.PLAIN, Integer.parseInt((String) font2SizeComboBox
            .getSelectedItem())));

    drawArea.setLineWidth(Integer.parseInt(lineWidthTextField.getText()));
    try {
      drawArea.setGrid(gridCheckbox.isSelected(), Integer
          .parseInt(gridSTextField.getText()));
    } catch (NumberFormatException nfe) {
      drawArea.setGrid(gridCheckbox.isSelected(), 25);
    }

    drawArea.setDefSC(tempSC);
    drawArea.setDefSTC(tempSTC);
    drawArea.setDefLTC(tempLTC);
    drawArea.setStateW(Integer.parseInt(SPWField.getText()));
    drawArea.setStateH(Integer.parseInt(SPHField.getText()));
    drawArea.updateGlobalTable();

    dispose();
  }// GEN-LAST:event_jButton2ActionPerformed

  /**
   * @param args
   *          the command line arguments
   */

  // GEN-BEGIN:variables
  // Variables declaration - do not modify
  private javax.swing.JComboBox font2ComboBox;
  private javax.swing.JComboBox font2SizeComboBox;
  private javax.swing.JButton fontColorButton;
  private javax.swing.JComboBox fontComboBox;
  private javax.swing.JComboBox fontSizeComboBox;
  private javax.swing.JPanel globalTablePanel;
  private javax.swing.JCheckBox gridCheckbox;
  private javax.swing.JTextField gridSTextField;
  private javax.swing.JTextField lineWidthTextField;
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JLabel jLabel1; // "space between columns"
  private javax.swing.JLabel jLabel2; // space between columns "pixels"
  private javax.swing.JLabel jLabel3; // "Table Font"
  private javax.swing.JLabel jLabel5; // "Font" in Draw Area box
  private javax.swing.JLabel jLabel7; // ?
  private javax.swing.JLabel jLabel8; // grid size "pixels"
  private javax.swing.JLabel jLabel9; // default state width "pixels"
  private javax.swing.JLabel jLabel10; // default state height "pixels"
  private javax.swing.JLabel jLabel11; // "Line Width"
  private javax.swing.JLabel jLabel12; // lineWidth "pixels"
  private javax.swing.JPanel jPanel2;
  private javax.swing.JTextField spaceTextField;
  private javax.swing.JCheckBox tableVisCheckbox;
  private javax.swing.JLabel SC1;
  private javax.swing.JLabel SC2;
  private javax.swing.JLabel STC1;
  private javax.swing.JLabel STC2;
  private javax.swing.JLabel LTC1;
  private javax.swing.JLabel LTC2;
  private javax.swing.JFormattedTextField SPHField;
  private javax.swing.JLabel SPW;
  private javax.swing.JLabel SPH;
  private javax.swing.JFormattedTextField SPWField;
  // End of variables declaration//GEN-END:variables

}
