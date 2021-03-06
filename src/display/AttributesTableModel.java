package display;

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

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import attributes.EnumVisibility;
import attributes.GlobalAttributes;
import attributes.ObjAttribute;
import entities.GeneralObj;

/**
 * A table model for the properties of an object.
 * It can be properties of a given MACHINE, of the INPUTS, OUTPUTS or of a STATE
 * or TRANSITION
 */
@SuppressWarnings("serial")
public class AttributesTableModel extends AbstractTableModel {

  boolean DEBUG = false;
  // String[] columnNames = {"Attribute Name", "Value",
  // "Visibility", "Type", "Comment", "Color" };
  String[] columnNames = { "Attribute Name", "Value",
      "Visibility", "Type", "Comment",
      "Color", "UserAtts", "ResetValue"
  }; // for state/trans edit boxes
  boolean global = false;

  /* The object the properties of which it it describing */
  GeneralObj obj;
  LinkedList<ObjAttribute> attrib;
  GlobalAttributes global_attributes;
  JDialog dialog;

  // int positionInGlobalList;

  AttributesTableModel(GeneralObj s, JDialog dia,
      GlobalAttributes globals) {
    obj = s;
    attrib = obj.getAttributeList();
    global_attributes = globals;
    dialog = dia;
    // positionInGlobalList = k;
  }

  AttributesTableModel(LinkedList<ObjAttribute> list,
      GlobalAttributes globals) {
    global = true;
    global_attributes = globals;
    attrib = list;
    // pz
    // columnNames = new String[] {"Attribute Name", "Default Value",
    // "Visibility", "Type","Comment", "Color"};
    columnNames = new String[] { "Attribute Name", "Default Value",
        "Visibility", "Type", "Comment",
        "Color", "UserAtts", "ResetValue"
    }; // for main att edit boxes
  }

  /* 3 Methods that need to be implemented (cf java AbstractTableModel) */
  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public int getRowCount() {
    return attrib.size();
  }

  @Override
  public Object getValueAt(int row, int col) {
    Object obj = attrib.get(row).get(col);
    if (col == 2) { // pz - transition?
      obj = (EnumVisibility) obj;
    }

    // Translate internal representation "reg" to "statebit"
    if (col == 3) {
      if (obj.equals(new String("reg")))
        obj = "statebit";
    }
    return obj;
  }

  // Get type for column
  public Class getColumnClass(int col) {
    return getValueAt(0, col).getClass();
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  // GLOBAL_FIXED can only be edited in global tab
  // ABS can't be edited anywhere
  public boolean isCellEditable(int row, int col) {
    if ((attrib.get(row).getEditable(col) == ObjAttribute.GLOBAL_FIXED && !global)
        || attrib.get(row).getEditable(col) == ObjAttribute.ABS)
      return false;
    else if (global && attrib.equals(global_attributes.getInputsAttributes())
        && (col == 2 || col == 5))
      return false;
    else
      return true;

  }

  public void setValueAt(Object value, int row, int col) {
    // 0: Name
    // 1: (Default) value
    // 2: Visibility
    // 3: Type
    // 4: Comment
    // 5: Color
    // 6: UserAtts
    // 7: Resetval

    // turn string into corresponding number
    if (col == 2) {
      value = (EnumVisibility) value;
    }

    // Translate "statebit" to internal representation "reg"
    if (col == 3 && value.equals("statebit")) {
      value = new String("reg");
    }

    // check for reserved words
    // Not sure if this is really necessary
    /*
     * if ( col == 1 &&
     * ( false
     * || value.equals("output")
     * )) {
     * JOptionPane.showMessageDialog(dialog,
     * "\"" + value + "\"" + " is a reserved word",
     * "error",
     * JOptionPane.ERROR_MESSAGE);
     * value = attrib.get(row).get(col);
     * }
     */

    // only flag, regd can have reset values
    if (false
        || (global && col == 7 && !value.equals("") && !((attrib
            .get(row)
            .getType()
            .equals("flag") || attrib.get(row).getType().equals("regdp"))))
        || (global && col == 3
            && !(value.equals("flag") || value.equals("regdp")) && !attrib.get(
            row).getresetval().equals(""))) {
      JOptionPane.showMessageDialog(dialog,
          "Only regdp and flag can have a reset value",
          "error",
          JOptionPane.ERROR_MESSAGE);
      value = attrib.get(row).get(col);
    }
    // flag type outputs must have null default value
    if (false
        || (global && col == 1 && !value.equals("") && attrib
            .get(row)
            .getType()
            .equals("flag"))
        || (global && col == 3 && value.equals("flag") && !attrib
            .get(row)
            .getValue()
            .equals(""))) {
      JOptionPane.showMessageDialog(dialog,
          "Flags cannot have default values",
          "error",
          JOptionPane.ERROR_MESSAGE);
      value = attrib.get(row).get(col);
    }

    // forces user to enter attribute name in outputs tab
    if (!global && col == 3 && value.equals("output")) {
      if (!checkOutputs(attrib.get(row))) {
        value = "";
        JOptionPane.showMessageDialog(dialog,
            "Attribute with that name must exist in global outputs tab",
            "error",
            JOptionPane.ERROR_MESSAGE);

      }
    }

    // first time type being set in outputs, create corresponding attribute in
    // state tab
    if (global
        && col == 3
        && attrib.equals(global_attributes.getOutputsAttributes())
        && (value.equals("regdp") || value.equals("comb")
            || value.equals("reg") || value.equals("flag"))
        && attrib.get(row).get(col).equals("")) {
      int[] editable = { ObjAttribute.GLOBAL_FIXED, ObjAttribute.GLOBAL_VAR,
          ObjAttribute.GLOBAL_VAR, ObjAttribute.GLOBAL_VAR,
          ObjAttribute.GLOBAL_VAR, ObjAttribute.GLOBAL_VAR,
          ObjAttribute.GLOBAL_VAR, ObjAttribute.GLOBAL_VAR
      };
      ObjAttribute newObj = new ObjAttribute(attrib.get(row).getName(), attrib
          .get(row)
          .getValue(),
          attrib.get(row).getVisibility(), "output", "", Color.black, "", "",
          editable);
      global_attributes.getStateAttributes().addLast(newObj);
    }

    // if rename something of type output
    if (global && col != 3
        && global_attributes.getOutputsAttributes().equals(attrib)
        && !attrib.get(row).get(col).equals(value)) {
      renameAttribute(3, attrib.get(row).getName(), col, value, row);
    }

    // force user to edit in outputs tab
    // changed
    if (global &&
        // can't edit anything but type in states
        (col != 3 && attrib.equals(global_attributes.getStateAttributes()) && attrib
            .get(row)
            .getType()
            .equals("output"))
        // can't edit default value in transitions
        // new fizzim.pl (>=4.3 uses default from outputs page)
        || (col == 1 && attrib.equals(global_attributes.getTransAttributes()) && attrib
            .get(row)
            .getType()
            .equals("output"))) {
      JOptionPane.showMessageDialog(dialog,
          "Must edit in output tab",
          "error",
          JOptionPane.ERROR_MESSAGE);
      value = attrib.get(row).get(col);
    }

    // if changing from comb to reg
    /*
     * if(attrib.equals(globalList.get(4)) &&
     * attrib.get(row).getType().equals("comb") && value.equals("reg"))
     * {
     * for(int i = 0; i < globalList.get(2).size(); i++)
     * {
     * if(attrib.get(row).getName().equals(globalList.get(2).get(i).getName()))
     * {
     * globalList.get(2).remove(i);
     * }
     * }
     * }
     */

    // dont set if nothing changes
    if (!attrib.get(row).get(col).equals(value))
      attrib.get(row).set(col, value);

    // set to local if different from global
    if (!global) {
      if (!checkValue(row, col, value)) {
        attrib.get(row).setEditable(col, ObjAttribute.LOCAL);
      } else
        attrib.get(row).setEditable(col, ObjAttribute.GLOBAL_VAR);
    }

    // checks for when name being changed
    if (attrib.get(row).getName().equals("name") && col == 1 && !global)
      obj.setName((String) value);

    // restore to default if empty string was entered
    if (col != 2 && value.equals("") && !global) {
      obj.updateAttrib(global_attributes);
    }

    fireTableCellUpdated(row, col);

  }

  private boolean checkValue(int row, int col, Object value) {
    LinkedList<ObjAttribute> list = obj.getAttributes(global_attributes);
    String name = attrib.get(row).getName();
    Object val = attrib.get(row).get(col);
    for (int i = 0; i < list.size(); i++) {
      ObjAttribute obj = list.get(i);
      if (name.equals(obj.getName()) && val.equals(obj.get(col)))
        return true;
    }
    return false;
  }

  private void renameAttribute(int t, String name, int col, Object value,
      int row) {
    // try to find coresponding field in states tab that is in the same
    // relative position
    // (fixes errors due to multiple fields with same names)
    int num = 0;
    boolean needed = true;

    for (int h = 0; h < global_attributes.getSpecificGlobalAttributes(t).size(); h++) {
      ObjAttribute obj = global_attributes
          .getSpecificGlobalAttributes(t)
          .get(h);
      // check if field is of type output in state tab
      if (obj.getType().equals("output") && t == 3 && num <= row) {
        if (num == row && obj.getName().equals(name)) {
          obj.set(col, value);
          needed = false;
          break;
        } else
          num++;
      }
    }

    if (needed) {
      for (int i = 0; i < global_attributes
          .getSpecificGlobalAttributes(t)
          .size(); i++) {
        ObjAttribute obj = global_attributes
            .getSpecificGlobalAttributes(t)
            .get(i);
        if (obj.getName().equals(name)) {
          obj.set(col, value);
          break;
        }
      }
    }

  }

  private boolean checkOutputs(ObjAttribute objAttribute) {
    LinkedList<ObjAttribute> outputList = global_attributes
        .getOutputsAttributes();
    String name = objAttribute.getName();
    for (int i = 0; i < outputList.size(); i++) {
      ObjAttribute obj = outputList.get(i);
      if (obj.getName().equals(name))
        return true;
    }
    return false;
  }

  private boolean checkName(LinkedList<ObjAttribute> linkedList, String name) {
    for (int i = 0; i < linkedList.size(); i++) {
      ObjAttribute obj = linkedList.get(i);
      if (obj.getName().equals(name))
        return true;
    }
    return false;
  }

}