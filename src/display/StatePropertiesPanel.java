package display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.table.TableColumn;

import attributes.EnumVisibility;
import attributes.ObjAttribute;
import entities.StateObj;
import gui.GeneralEditorWindow;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

@SuppressWarnings("serial")
public class StatePropertiesPanel extends JPanel {
  private StateObj state;
  private DrawArea drawArea;
  private JColorChooser colorChooser;
  private LinkedList<LinkedList<ObjAttribute>> globalList;
  private JDialog parent_window;
  private javax.swing.JButton SPDelete;
  private javax.swing.JLabel SPH;
  private javax.swing.JFormattedTextField SPHField;
  private javax.swing.JLabel SPLabel;
  private javax.swing.JButton SPNew;
  private javax.swing.JScrollPane SPScroll;
  private javax.swing.JTable SPTable;
  private javax.swing.JLabel SPW;
  private javax.swing.JLabel SPC;
  private javax.swing.JFormattedTextField SPWField;
  private String oldName;

  /**
   * 
   * @param parent_window
   *          The parent window
   * @param DA
   * @param s
   */
  public StatePropertiesPanel(GeneralEditorWindow parent_window, DrawArea DA,
      StateObj s) {
    state = s;
    drawArea = DA;
    colorChooser = drawArea.getColorChooser();
    globalList = drawArea.getGlobalList();

    oldName = new String(state.getName());
    this.parent_window = parent_window;
    initComponents();

    /*
     * We add the update and cancel actions to the OK and Cancel button of the
     * parent window
     */
    parent_window.getBtnOk().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        SPOKActionPerformed(evt);
      }
    });

    parent_window.getBtnCancel().addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        SPCancelActionPerformed(evt);
      }
    });
  }

  // End of variables declaration//GEN-END:variables
  private void initComponents() {
    SPLabel = new JLabel();
    SPScroll = new JScrollPane();
    SPTable = new JTable();
    SPW = new JLabel();
    SPH = new JLabel();
    SPC = new JLabel();
    SPWField = new JFormattedTextField(NumberFormat
        .getIntegerInstance());
    SPHField = new JFormattedTextField(NumberFormat
        .getIntegerInstance());
    SPNew = new JButton();
    SPDelete = new JButton();

    SPLabel.setText("Edit the properties of the selected state:");

    // Type column
    SPTable.setModel(new MyTableModel(state, parent_window, globalList, 3));
    SPTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    // use dropdown boxes
    String[] options = new String[] { "No", "Yes", "Only non-default" };
    TableColumn column = SPTable.getColumnModel().getColumn(2);
    column.setCellEditor(new MyJComboBoxEditor(options));

    // Color column
    column = SPTable.getColumnModel().getColumn(5);
    column.setPreferredWidth(SPTable.getRowHeight());
    column.setCellEditor(new MyJColorEditor(colorChooser));
    column.setCellRenderer(new MyJColorRenderer());

    SPNew.setVisible(false);
    SPDelete.setVisible(false);

    SPScroll.setViewportView(SPTable);

    SPW.setText("Width:");
    SPH.setText("Height:");

    SPC.setPreferredSize(new Dimension(50, 20));
    SPC.setMinimumSize(new Dimension(50, 20));
    SPC.setOpaque(true);
    SPC.setVisible(true);

    // set background color to color of transition and add action listener
    SPC.setBackground(state.getColor());
    SPC.setBorder(new LineBorder(Color.black, 1));
    SPC.addMouseListener(new MouseListener() {

      ActionListener colorSel = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          SPC.setBackground(colorChooser.getColor());
          state.setColor(colorChooser.getColor());
        }
      };

      public void mouseClicked(MouseEvent e)
      {
        JDialog dialog;
        dialog = JColorChooser.createDialog(parent_window, "Choose Color",
            true,
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

    SPWField.setValue(new Integer(state.getWidth()));
    SPWField.setColumns(10);
    SPHField.setValue(new Integer(state.getHeight()));
    SPHField.setColumns(10);

    SPNew.setText("New");
    SPNew.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        SPNewActionPerformed(evt);
      }
    });

    SPDelete.setText("Delete");
    SPDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        SPDeleteActionPerformed(evt);
      }
    });

    GroupLayout layout = new GroupLayout(this);
    layout.setHorizontalGroup(layout
        .createParallelGroup(Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout
                .createParallelGroup(Alignment.LEADING)
                .addComponent(SPScroll, GroupLayout.DEFAULT_SIZE,
                    670, Short.MAX_VALUE)
                .addComponent(SPLabel)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(
                        Alignment.LEADING)
                        .addComponent(SPW)
                        .addComponent(SPH))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(
                        Alignment.LEADING)
                        .addComponent(SPHField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                        .addComponent(SPWField,
                            GroupLayout.PREFERRED_SIZE,
                            GroupLayout.DEFAULT_SIZE,
                            GroupLayout.PREFERRED_SIZE))
                    .addGap(42)
                    .addComponent(SPC,
                        GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(SPNew)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(SPDelete)))
            .addContainerGap())
        );
    layout.setVerticalGroup(
        layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SPLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(SPScroll, GroupLayout.PREFERRED_SIZE, 151,
                    GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(SPNew)
                    .addComponent(SPDelete))
                .addGap(22)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(SPW)
                    .addComponent(SPWField, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                    .addComponent(SPC, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(SPH)
                    .addComponent(SPHField, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    setLayout(layout);
    // pack();
  }

  private void SPNewActionPerformed(ActionEvent evt) {
    ObjAttribute newObj =
        new ObjAttribute("", "", EnumVisibility.NO, "", "", Color.black, "", "");
    state.getAttributeList().addLast(newObj);
    SPTable.revalidate();
  }

  private void SPDeleteActionPerformed(ActionEvent evt) {
    // delete selected rows
    int[] rows = SPTable.getSelectedRows();
    for (int i = rows.length - 1; i > -1; i--)
    {
      int type = state.getAttributeList().get(rows[i]).getEditable(0);
      if (type != ObjAttribute.GLOBAL_FIXED && type != ObjAttribute.ABS)
      {
        state.getAttributeList().remove(rows[i]);
        SPTable.revalidate();
      }
      else
      {
        JOptionPane.showMessageDialog(this,
            "Cannot delete a global attribute.\n"
                + "Must be removed from global attribute properties.",
            "error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
    // notify attribute list?
  }

  private void SPOKActionPerformed(ActionEvent evt) {
    SPTable.editCellAt(0, 0);
    if (drawArea.checkStateNames())
    {
      // temp
      try {
        SPWField.commitEdit();
        SPHField.commitEdit();
      } catch (ParseException e) {
        // TODsO Auto-generated catch block
        e.printStackTrace();
      }
      for (int j = 0; j < globalList.get(0).size(); j++)
      {
        if (globalList.get(0).get(j).getName().equals("reset_state")
            && globalList.get(0).get(j).getValue().equals(oldName))
        {
          globalList.get(0).get(j).setValue(state.getName());
        }
      }
      int width = ((Number) SPWField.getValue()).intValue();
      int height = ((Number) SPHField.getValue()).intValue();
      state.setSize(width, height);
      // make transitions redraw
      state.setStateModifiedTrue();
      drawArea.updateTransitions();
      drawArea.updateStates();
      drawArea.updateGlobalTable();
      drawArea.commitUndo();
      parent_window.dispose();
    }
    else
    {
      JOptionPane.showMessageDialog(this,
          "Two different states cannot have the same name.",
          "error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void SPCancelActionPerformed(ActionEvent evt) {
    drawArea.cancel();
    parent_window.dispose();
  }
}
