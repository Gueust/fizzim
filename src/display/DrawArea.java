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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.RepaintManager;

import locale.UTF8Control;
import attributes.GlobalAttributes;
import entities.GeneralObj;
import entities.GeneralObjType;
import entities.LoopbackTransitionObj;
import entities.SelectOptions;
import entities.StateObj;
import entities.StateTransitionObj;
import entities.TextObj;
import entities.TransitionObj;
import gui.StateEditorWindow;
import gui.TransitionEditorWindow;

@SuppressWarnings("serial")
public class DrawArea extends JPanel implements MouseListener,
    MouseMotionListener, ActionListener, Printable {

  private static final boolean PRINT_EVENTS_LOG = false;

  private static final ResourceBundle locale =
      ResourceBundle.getBundle("locale.Editors", new UTF8Control());

  /** Collection of the GeneralObj to display */
  private LinkedList<GeneralObj> objList;
  // holds previous lists of objects
  private Vector<Collection<GeneralObj>> undoList;

  // temp lists
  private LinkedList<GeneralObj> tempList;

  // keeps track of current position in undo array
  private int currUndoIndex;

  // triggered when undo needs to be committed
  private boolean toCommit = false;

  // hold right click position
  private int rXTemp, rYTemp;

  // hold clicked state
  private GeneralObj tempObj;
  private GeneralObj tempOld;
  private GeneralObj tempClone;

  // for multiple object select
  /** True iff several objects are selected ? */
  private boolean multipleSelect = false;
  /** True iff multiple object are selected ? */
  private boolean objsSelected = false;
  private int mXTemp = 0;
  private int mYTemp = 0;
  private int mX0, mY0, mX1, mY1;
  /**
   * The set of all the selected objects.
   */
  private Set<GeneralObj> selectedObjects = new LinkedHashSet<>();
  // private boolean ctrlDown = false;

  // Fonts
  private Font currFont = new Font("Arial", Font.PLAIN, 11);
  private Font tableFont = new Font("Arial", Font.PLAIN, 11);

  // Global color chooser
  private JColorChooser colorChooser = new JColorChooser();

  private boolean loading = false;

  private boolean Redraw = false;

  // Default settings for global table
  private boolean tableVis = true;
  private Color tableColor = Color.black;
  private Color defaultStatesColor = Color.black;
  private Color defaultStateTransitionsColor = Color.black;
  private Color defaultTransitionsColor = Color.black;

  // Default size of states
  private int defaultStatesWidth = 130;
  private int defaultStatesHeight = 130;

  // line widths
  private int LineWidth = 1;

  // list of global lists
  private GlobalAttributes global_attributes;

  // parent frame
  private FizzimGui fizzim_gui;

  // keeps track if file is modified since last opening/saving
  private boolean fileModified = false;

  // used for auto generation of state and transition names
  private int createSCounter = 0, createTCounter = 0;

  // pages
  private int currPage = 1;

  /* Double click delay in ms */
  private static final int DOUBLE_CLICK_TIME = 200;
  /* Time of the previous click */
  private long lastClick = 0;

  // lock to grid settings
  private boolean grid = false;
  private int gridS = 25;

  // global table, default tab settings
  private int space = 20;

  // private GlobalAttributes global_attributes;
  public DrawArea(GlobalAttributes globals) {
    global_attributes = globals;

    // create arrays to store created objects
    objList = new LinkedList<>();
    undoList = new Vector<>();
    tempList = new LinkedList<>();
    this.setFocusable(true);
    this.requestFocus();
    currUndoIndex = -1;

    // global attributes stored at index 0 of object array
    // objList.add(globals);

    TextObj globalTable = new TextObj(10, 10, globals, tableFont);

    objList.add(globalTable);
    undoList.add(objList);
    currUndoIndex++;

    setBackground(Color.WHITE);
    addMouseListener(this);
    addMouseMotionListener(this);

  }

  public void paintComponent(Graphics g) {
    Graphics2D g2D = (Graphics2D) g;
    super.paintComponent(g2D);

    g2D.setFont(currFont);
    g2D.setStroke(new BasicStroke(getLineWidth()));
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    // paint all objects
    if (objList != null) {
      for (GeneralObj s : objList) {
        s.paintComponent(g2D, currPage);
      }
    }
    if (multipleSelect) {
      g2D.setColor(Color.RED);
      g2D.drawRect(mX0, mY0, mX1 - mX0, mY1 - mY0);
    }
    if (loading) {
      updateGlobalTable();
      loading = false;
      repaint();
    }
  }

  public boolean canUndo() {
    if (currUndoIndex >= 0) {
      return true;
    } else {
      return false;
    }
  }

  public void undo() {
    // store current array on undo list if it isn't already there
    if (currUndoIndex + 1 == undoList.size())
      undoList.add(objList);
    // replace objlist with a previous list
    if (currUndoIndex > 0) {
      objList = (LinkedList<GeneralObj>) undoList.elementAt(currUndoIndex);
      currUndoIndex--;
    }
    // unselect all states
    for (GeneralObj s : objList) {
      s.unselect();
    }
    objsSelected = false;

    // update
    // global_attributes = (GlobalAttributes) objList.get(0);
    updateStates();
    updateTrans();
    updateGlobalTable();
    fizzim_gui.updateGlobal(global_attributes);
    repaint();

  }

  public boolean canRedo() {
    if (currUndoIndex < undoList.size() - 2) {
      return true;
    } else {
      return false;
    }
  }

  public void redo() {
    // if redo is possible, replace objlist with wanted list
    if (currUndoIndex < undoList.size() - 2) {
      objList =
          (LinkedList<GeneralObj>) undoList.elementAt(currUndoIndex + 2);
      currUndoIndex++;
    }
    // global_attributes = (GlobalAttributes) objList.get(0);
    updateStates();
    updateTrans();
    updateGlobalTable();
    fizzim_gui.updateGlobal(global_attributes);
    repaint();
  }

  /*
   * The following two methods create an undo point. It is not committed to undo
   * list yet
   * because an undo point is not needed if the user is just clicking to select
   * an
   * object (this method is triggered on mouse down)
   * In this method, a temp list is created to hold all the pointers to objects
   * before any modification occurs. The objects to be modified, and all objects
   * connected
   * to them, are then cloned, and their clones are pointed to by objlist.
   */

  // this method is called whenever a global attribute is about to be modified
  public GlobalAttributes setUndoPoint() {
    tempList = null;
    tempList = (LinkedList<GeneralObj>) objList.clone();
    // TODO remove this : GlobalAttributes oldGlobal = (GlobalAttributes)
    // objList.get(0);
    GlobalAttributes newGlobal = global_attributes.clone();

    // TODO: remove this: objList.set(0, newGlobal);
    global_attributes = newGlobal;

    return newGlobal;
  }

  // for multiple select, clone all selected objects
  @SuppressWarnings("unchecked")
  private void setUndoPointMultiple() {
    tempList = null;
    tempList = (LinkedList<GeneralObj>) objList.clone();

    // go through all selected objects and clone
    for (GeneralObj oldObj : selectedObjects) {
      GeneralObj clonedObj = null;
      try {
        clonedObj = (GeneralObj) oldObj.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }

      // if a state, create clone of children transitions
      if (oldObj.getType() == GeneralObjType.STATE) {

        Iterator<GeneralObj> general_obj_iterator = objList.iterator();
        while (general_obj_iterator.hasNext()) {
          GeneralObj s = general_obj_iterator.next();

          // check all objects that have to be modified state as a parent
          if (s.getType() != GeneralObjType.STATE && s.containsParent(oldObj)) {
            GeneralObj clonedObj2 = null;
            try {
              clonedObj2 = (GeneralObj) s.clone();
            } catch (CloneNotSupportedException e) {
              e.printStackTrace();
            }

            // replace old obj link in trans obj with new cloned one
            clonedObj2.notifyChange(oldObj, clonedObj);
            clonedObj2.setParentModified(true);

            // add cloned child to object list
            // TODO: check this part.
            // int objListIndex = objList.indexOf(s);
            // objList.set(objListIndex, clonedObj2);
            general_obj_iterator.remove();
            objList.add(clonedObj2);

          }
        }
      }
      objList.remove(oldObj);
      objList.add(clonedObj);
    }
  }

  /**
   * 
   * @param oldObj
   * @param type
   */
  private void setUndoPoint(GeneralObj oldObj) {
    tempList = null;
    tempList = (LinkedList<GeneralObj>) objList.clone();

    if (oldObj != null) {
      GeneralObj clonedObj = null;
      try {
        clonedObj = (GeneralObj) oldObj.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }

      tempOld = oldObj;
      tempClone = clonedObj;

      GeneralObjType type = oldObj.getType();
      // if a state, create clone of children transitions
      if (type == GeneralObjType.STATE) {
        fizzim_gui.updateGlobal(setUndoPoint());
        LinkedList<GeneralObj> to_be_added_after_iteration = new LinkedList<>();
        Iterator<GeneralObj> general_obj_iterator = objList.iterator();
        while (general_obj_iterator.hasNext()) {
          GeneralObj s = general_obj_iterator.next();

          // check all objects that have to be modified state as a parent
          if (s.getType() != GeneralObjType.STATE && s.containsParent(oldObj)) {
            GeneralObj clonedObj2 = null;
            try {
              clonedObj2 = (GeneralObj) s.clone();
            } catch (CloneNotSupportedException e) {
              e.printStackTrace();
            }

            // replace old obj link in trans obj with new cloned one
            clonedObj2.notifyChange(oldObj, clonedObj);
            clonedObj2.setParentModified(true);

            // add cloned child to object list
            // TODO: check this part.
            // int objListIndex = objList.indexOf(s);
            // objList.set(objListIndex, clonedObj2);
            general_obj_iterator.remove();
            to_be_added_after_iteration.add(clonedObj2);

          }
        }
        /** We add the created object (we cannot add it during the iteration) */
        objList.addAll(to_be_added_after_iteration);
      }
      objList.remove(oldObj);
      objList.add(clonedObj);
    }
  }

  // used when properties is cancelled
  public void cancel() {
    objList = tempList;
  }

  /**
   * Register the current state of the drawArea as the first entry in the
   * history.
   */
  // If a modification actually occurred,
  // the temp list is stored to the undo list array
  public void commitUndo() {
    int size = undoList.size();
    // clears redo points ahead of current position
    if (currUndoIndex < size - 1) {

      for (int i = size - 1; i > currUndoIndex; i--)
        undoList.remove(i);
    }
    undoList.add(tempList);
    currUndoIndex++;
    fileModified = true;
    repaint();
  }

  public void mouseClicked(MouseEvent e) {
    if (PRINT_EVENTS_LOG) {
      System.out.println("mouseClicked:" + " Button:" + e.getButton() +
          " Modifiers:" + e.getModifiers() + " Popup Trigger:"
          + e.isPopupTrigger()
          + " ControlDown:" + e.isControlDown());
    }
  }

  public void mouseEntered(MouseEvent e) {
    if (PRINT_EVENTS_LOG) {
      System.out.println("mouseEntered:" + " Button:" + e.getButton() +
          " Modifiers:" + e.getModifiers() + " Popup Trigger:"
          + e.isPopupTrigger()
          + " ControlDown:" + e.isControlDown());
    }
  }

  public void mouseExited(MouseEvent e) {
    if (PRINT_EVENTS_LOG) {
      System.out.println("mouseeExited:" + " Button:" + e.getButton() +
          " Modifiers:" + e.getModifiers() + " Popup Trigger:"
          + e.isPopupTrigger()
          + " ControlDown:" + e.isControlDown());
    }
  }

  public void mouseHandle(MouseEvent e) {

  }

  /**
   * TODO: think about the use of setUndoPoint
   */
  public void mousePressed(MouseEvent e) {
    if (PRINT_EVENTS_LOG) {
      System.out.println("mousePressed:" + " Button:" + e.getButton() +
          " Modifiers:" + e.getModifiers() + " Popup Trigger:"
          + e.isPopupTrigger()
          + " ControlDown:" + e.isControlDown());
    }
    GeneralObj bestMatch = null;
    boolean doubleClick = false;
    // check for double click
    if (e.getWhen() - lastClick < DOUBLE_CLICK_TIME
        && e.getButton() == MouseEvent.BUTTON1 && e.getModifiers() != 20)
      doubleClick = true;

    lastClick = e.getWhen();

    // check for ctrl click to select multiple
    if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1) {
      // store selected objects and their indices
      int numbSel = 0;
      Set<GeneralObj> tempIndices = new LinkedHashSet<>();
      for (GeneralObj obj : objList) {
        if (obj.getType() == GeneralObjType.STATE
            || obj.getType() == GeneralObjType.TEXT) {
          if (obj.getSelectStatus() != SelectOptions.NONE) {
            // deselect if clicked on when selected
            if (obj.setSelectStatus(e.getX(), e.getY())) {
              obj.unselect();
            } else {
              obj.setSelectStatus(true);
              numbSel++;
              tempIndices.add(obj);
            }
          } else if (obj.setSelectStatus(e.getX(), e.getY())) {
            numbSel++;
            tempIndices.add(obj);
          }
        } else {
          obj.unselect();
        }
      }
      // if multiple items selected so far, update global selection list.
      if (numbSel > 1) {
        selectedObjects = tempIndices;
        objsSelected = true;
      }
    } else if (objsSelected) { // if multiple object selected
      setUndoPointMultiple();
      if (e.getButton() == MouseEvent.BUTTON1 && e.getModifiers() != 20) {
        boolean move = false;

        for (GeneralObj obj : selectedObjects) {
          if ((obj.getType() == GeneralObjType.STATE || obj.getType() == GeneralObjType.TEXT)
              && obj.setBoxSelectStatus(e.getX(), e.getY())) {
            move = true;
          }
        }
        // if click outside, unselect all
        if (!move) {
          objsSelected = false;
          unselectObjs();
        }
      } else if (e.getButton() == MouseEvent.BUTTON3 || e.getModifiers() == 20) {
        createPopup(null, e);
      }
    } else {
      // if object already selected
      for (GeneralObj s : (Collection<GeneralObj>) objList.clone()) {
        if (s.getSelectStatus() != SelectOptions.NONE
            && s.getPage() == currPage
            && s.setSelectStatus(e.getX(), e.getY())) {
          bestMatch = s;

          if (!doubleClick) {
            // if right click, create popup menu
            if (e.getButton() == MouseEvent.BUTTON3 || e.getModifiers() == 20) {
              setUndoPoint(s);
              createPopup(s, e);
            } else {
              setUndoPoint(s);
            }
            break;
          } else {
            if (s.getType() == GeneralObjType.STATE) {
              new StateEditorWindow(fizzim_gui, this, (StateObj) s);
            } else if (s.getType() == GeneralObjType.TRANSITION) {
              Vector<StateObj> stateObjs = new Vector<StateObj>();
              for (GeneralObj obj : objList) {
                if (obj.getType() == GeneralObjType.STATE)
                  stateObjs.add((StateObj) obj);
              }
              new TransitionEditorWindow(fizzim_gui, this,
                  (StateTransitionObj) s,
                  stateObjs, false, null);
              // new TransProperties(this, frame, true, (StateTransitionObj) s,
              // stateObjs, false, null)
              // .setVisible(true);
            } else if (s.getType() == GeneralObjType.LOOPBACK_TRANSITION) {
              Vector<StateObj> stateObjs = new Vector<StateObj>();
              for (GeneralObj obj : objList) {
                if (obj.getType() == GeneralObjType.STATE)
                  stateObjs.add((StateObj) obj);
              }
              new TransitionEditorWindow(fizzim_gui, this,
                  (LoopbackTransitionObj) s,
                  stateObjs, true, null);
              // new TransProperties(this, frame, true, (LoopbackTransitionObj)
              // s,
              // stateObjs, true, null)
              // .setVisible(true);
            } else if (s.getType() == GeneralObjType.TEXT) {
              editText((TextObj) s);
            }
          }
        }
      }

      // check for text at mouse location
      if (bestMatch == null) {
        for (GeneralObj s : objList) {
          if (s.getType() == GeneralObjType.TEXT
              && s.getPage() == currPage
              && s.setSelectStatus(e.getX(), e.getY())) {
            bestMatch = s;

            if (e.getButton() == MouseEvent.BUTTON3 || e.getModifiers() == 20) {
              setUndoPoint(s);
              createPopup(s, e);
            } else {
              setUndoPoint(s);
            }
            break;
          }
        }
      }

      // check for transition at mouse position
      if (bestMatch == null) {
        for (GeneralObj s : objList) {
          if ((s.getType() == GeneralObjType.TRANSITION ||
              s.getType() == GeneralObjType.LOOPBACK_TRANSITION)
              && s.getPage() == currPage
              && s.setSelectStatus(e.getX(), e.getY())) {
            bestMatch = s;

            if (e.getButton() == MouseEvent.BUTTON3 || e.getModifiers() == 20) {
              setUndoPoint(s);
              createPopup(s, e);
            } else {
              setUndoPoint(s);
            }
            break;
          }
        }
      }

      // if no transitions found at that position, look through state objects
      if (bestMatch == null) {
        for (GeneralObj s : objList) {
          if (s.getType() == GeneralObjType.STATE
              && s.getPage() == currPage
              && s.setSelectStatus(e.getX(), e.getY())) {
            bestMatch = s;

            if (e.getButton() == MouseEvent.BUTTON3 || e.getModifiers() == 20) {
              setUndoPoint(s);
              createPopup(s, e);
            } else {
              setUndoPoint(s);
            }
            break;
          }
        }
      }

      // if nothing is clicked on, and right click
      // TODO : Is the meaning of 20, MouseEvent.NOBUTTON ?
      if (bestMatch == null
          && (e.getButton() == MouseEvent.BUTTON3 || e.getModifiers() == 20)) {
        createPopup(e);
        setUndoPoint(null);
      }

      // now do multiple select if still nothing found
      if (bestMatch == null && e.getButton() == MouseEvent.BUTTON1
          && e.getModifiers() != 20) {
        mXTemp = e.getX();
        mYTemp = e.getY();
        mX0 = 0;
        mY0 = 0;
        mX1 = 0;
        mY1 = 0;
        multipleSelect = true;
        objsSelected = false;
        selectedObjects.clear();
      }

      repaint();
    }
  }

  public void mouseReleased(MouseEvent e) {
    if (PRINT_EVENTS_LOG) {
      System.out.println("mouseReleased:" + " Button:" + e.getButton() +
          " Modifiers:" + e.getModifiers() + " Popup Trigger:"
          + e.isPopupTrigger()
          + " ControlDown:" + e.isControlDown());
    }
    multipleSelect = false;
    if (!objsSelected) {
      selectedObjects.clear();
    }

    // done modifying all objects, so notify state objects that they are done
    // changing
    // this means transition object won't have to re-calculate connection points

    toCommit = false;

    for (GeneralObj t : objList) {
      // check for modified state
      if (t.isModified() && t.getType() == GeneralObjType.STATE) {
        toCommit = true;
        for (GeneralObj obj : objList) {
          if (obj.getType() != GeneralObjType.STATE && obj.isParentModified())
            obj.updateObj();
          obj.setParentModified(false);
        }
      }

      // check for modified line or text box
      if ((t.getType() == GeneralObjType.TRANSITION ||
          t.getType() == GeneralObjType.LOOPBACK_TRANSITION)
          && t.isModified()) {
        toCommit = true;
        for (GeneralObj obj : objList) {
          if ((obj.getType() == GeneralObjType.TEXT) && obj.isParentModified())
            obj.updateObj();
          obj.setParentModified(false);
        }
      }

      if (t.getType() == GeneralObjType.TEXT && t.isModified()) {
        toCommit = true;
      }
      t.setModified(false);
    }

    if (toCommit)
      commitUndo();

    repaint();
  }

  public void updateTransitions() {
    for (GeneralObj obj : objList) {
      if (obj.getType() != GeneralObjType.STATE && obj.isParentModified())
        obj.updateObj();
    }
  }

  public void mouseDragged(MouseEvent arg0) {
    /* keep movement within page */
    int x = arg0.getX();
    int y = arg0.getY();
    if (x < 0) {
      x = 0;
    }
    if (y < 0) {
      y = 0;
    }
    if (x > fizzim_gui.maxW) {
      x = fizzim_gui.maxW;
    }
    if (y > fizzim_gui.maxH) {
      y = fizzim_gui.maxH;
    }

    /*
     * Move single (!multipleSelect) object if multiple select is off
     * (!isControlDown())
     */
    if (!multipleSelect && !arg0.isControlDown() &&
        (arg0.getModifiers() == InputEvent.BUTTON1_MASK)) {

      for (GeneralObj s : objList) {
        if (s.getSelectStatus() != SelectOptions.NONE) {
          s.adjustShapeOrPosition(x, y);
          for (GeneralObj obj : objList) {
            if (obj.getType() != GeneralObjType.STATE && obj.isParentModified())
              obj.updateObj();
          }
          // break;
        }
      }
      repaint();
    }

    /* if multiple select is on, then check if any objects inside yet */
    else if (!arg0.isControlDown() &&
        (arg0.getModifiers() == InputEvent.BUTTON1_MASK)) {
      // Correct box coordinates
      if (x < mXTemp) {
        mX0 = x;
        mX1 = mXTemp;
      } else {
        mX0 = mXTemp;
        mX1 = x;
      }
      if (y < mYTemp) {
        mY0 = y;
        mY1 = mYTemp;
      } else {
        mY0 = mYTemp;
        mY1 = y;
      }

      int tempNumb = 0;
      objsSelected = false;
      selectedObjects.clear();
      for (GeneralObj s : objList) {
        if ((s.getType() == GeneralObjType.STATE || s.getType() == GeneralObjType.TEXT)
            && s.setBoxSelectStatus(mX0, mY0, mX1, mY1)) {
          tempNumb++;
          selectedObjects.add(s);
        }
      }
      if (tempNumb > 1)
        objsSelected = true;
      else
        selectedObjects.clear();
      repaint();
    }
  }

  public void mouseMoved(MouseEvent arg0) {

  }

  /**
   * Open the panel associated with a Right Click on a component
   * 
   * @param obj
   *          The selected component on which the right click is performed.
   * @param e
   *          The MouseEvent of the click (required for the positioning of the
   *          panel).
   */
  private void createPopup(GeneralObj obj, MouseEvent e) {
    /* Store the location of click and the object that is clicked on */
    rXTemp = e.getX();
    rYTemp = e.getY();
    tempObj = obj;

    JMenuItem menuItem;

    /* Create the popup menu. */
    JPopupMenu popup = new JPopupMenu();

    /* Create the submenu for moving the object to an other page */
    JMenu pages = new JMenu(locale.getString("drawArea_move_to_page"));

    for (int i = 1; i < fizzim_gui.getPages(); i++) {
      if (i != currPage) {
        menuItem = new JMenuItem(fizzim_gui.getPageName(i));
        menuItem.addActionListener(this);
        pages.add(menuItem);
      }
    }
    /* What is that for ? */
    if (obj == null) {
      popup.add(pages);
    }

    /* If the object is a sate */
    if (obj != null && obj.getType() == GeneralObjType.STATE) {
      menuItem = new JMenuItem(locale
          .getString("drawArea_add_loopback_transition"));
      menuItem.setMnemonic(KeyEvent.VK_L);
      menuItem.addActionListener(this);
      popup.add(menuItem);

      JMenu states = new JMenu(locale.getString("drawArea_add_transition_to"));
      states.setMnemonic(KeyEvent.VK_T);
      states.setDisplayedMnemonicIndex(10);
      for (GeneralObj obj1 : objList) {
        if (obj1.getType() == GeneralObjType.STATE
            && !obj.getName().equals(obj1.getName())) {
          menuItem = new JMenuItem(obj1.getName());
          menuItem.addActionListener(this);
          states.add(menuItem);
        }
      }
      popup.add(states);

      menuItem = new JMenuItem(locale
          .getString("drawArea_edit_state_properties"));
      menuItem.setMnemonic(KeyEvent.VK_E);
      menuItem.addActionListener(this);
      popup.add(menuItem);
      popup.add(pages);
    }
    /* If the object is a common transition */
    if (obj != null && obj.getType() == GeneralObjType.TRANSITION) {

      menuItem = new JMenuItem(locale
          .getString("drawArea_edit_transition_properties"));
      menuItem.setMnemonic(KeyEvent.VK_E);
      menuItem.addActionListener(this);
      popup.add(menuItem);
      if (obj.getSelectStatus() == SelectOptions.TXT) {
        JMenu pages2 = new JMenu(locale.getString("drawArea_move_to_page"));
        StateTransitionObj sobj = (StateTransitionObj) obj;
        for (int i = 1; i < fizzim_gui.getPages(); i++) {
          if (i != currPage && (i == sobj.getEPage() || i == sobj.getSPage())) {
            menuItem = new JMenuItem(fizzim_gui.getPageName(i));
            menuItem.addActionListener(this);
            pages2.add(menuItem);
          }
        }
        popup.add(pages2);
      }

    }
    /* If the object is a loopback transition */
    if (obj != null && obj.getType() == GeneralObjType.LOOPBACK_TRANSITION) {
      menuItem = new JMenuItem(locale
          .getString("drawArea_edit_loopback_properties"));
      menuItem.setMnemonic(KeyEvent.VK_E);
      menuItem.addActionListener(this);
      popup.add(menuItem);
    }
    /* If the object is a text */
    if (obj != null && obj.getType() == GeneralObjType.TEXT) {
      menuItem = new JMenuItem(locale.getString("drawArea_edit_text"));
      menuItem.setMnemonic(KeyEvent.VK_E);
      menuItem.addActionListener(this);
      popup.add(menuItem);
      popup.add(pages);
    }

    popup.show(e.getComponent(), e.getX(), e.getY());
  }

  public void createPopup(MouseEvent e) {
    rXTemp = e.getX();
    rYTemp = e.getY();
    tempObj = null;
    JMenuItem menuItem;

    // Create the popup menu.
    JPopupMenu popup = new JPopupMenu();
    menuItem = new JMenuItem(locale.getString("drawArea_quick_new_state"));
    menuItem.setMnemonic(KeyEvent.VK_Q);
    menuItem.addActionListener(this);
    popup.add(menuItem);
    menuItem = new JMenuItem(locale.getString("drawArea_new_state"));
    menuItem.setMnemonic(KeyEvent.VK_S);
    menuItem.addActionListener(this);
    popup.add(menuItem);
    menuItem = new JMenuItem(
        locale.getString("drawArea_new_state_transition"));
    menuItem.setMnemonic(KeyEvent.VK_T);
    menuItem.setDisplayedMnemonicIndex(10);
    menuItem.addActionListener(this);
    popup.add(menuItem);
    menuItem = new JMenuItem(
        locale.getString("drawArea_new_loopback_transition"));
    menuItem.setMnemonic(KeyEvent.VK_L);
    menuItem.addActionListener(this);
    popup.add(menuItem);
    menuItem = new JMenuItem(
        locale.getString("drawArea_new_free_text"));
    menuItem.setMnemonic(KeyEvent.VK_F);
    menuItem.addActionListener(this);
    popup.add(menuItem);

    popup.show(e.getComponent(), e.getX(), e.getY());

  }

  // called when item on popup menu is selected
  /**
   * @brief
   *        Executed when we do any creation action.
   * 
   * @details When we click right on the draw area (on an empty or filled
   *          space), a menu is displayed. When selection any of the options,
   *          this function is called.
   */
  public void actionPerformed(ActionEvent e) {
    JMenuItem source = (JMenuItem) (e.getSource());

    // if cloned for undo
    if (tempObj == tempOld)
      tempObj = tempClone;

    String input = source.getText();

    if (input == locale.getString("drawArea_edit_text")) {
      editText((TextObj) tempObj);
    } else if (input == locale.getString("drawArea_edit_state_properties")) {
      new StateEditorWindow(fizzim_gui, this, (StateObj) tempObj);
    } else if (input == locale.getString("drawArea_edit_loopback_properties")) {
      Vector<StateObj> stateObjs = new Vector<StateObj>();
      for (GeneralObj obj : objList) {
        if (obj.getType() == GeneralObjType.STATE)
          stateObjs.add((StateObj) obj);
      }
      new TransitionEditorWindow(fizzim_gui, this,
          (LoopbackTransitionObj) tempObj,
          stateObjs, true, null);
    } else if (input == locale.getString("drawArea_edit_transition_properties")) {
      Vector<StateObj> stateObjs = new Vector<StateObj>();
      for (GeneralObj obj : objList) {
        if (obj.getType() == GeneralObjType.STATE)
          stateObjs.add((StateObj) obj);
      }
      new TransitionEditorWindow(fizzim_gui, this,
          (StateTransitionObj) tempObj,
          stateObjs, false, null);
    } else if (input == locale.getString("drawArea_quick_new_state")) {
      GeneralObj state;
      do {
        state = new StateObj(global_attributes,
            rXTemp - defaultStatesWidth / 2, rYTemp
                - defaultStatesHeight / 2,
            rXTemp + defaultStatesWidth / 2, rYTemp + defaultStatesHeight / 2,
            createSCounter, currPage,
            defaultStatesColor, grid, gridS);
        createSCounter++;

      } while (checkStateNames(state.getName()));
      objList.add(state);
      state.updateAttrib(global_attributes);
      commitUndo();
    } else if (input == locale.getString("drawArea_new_state")) {
      StateObj state = new StateObj(global_attributes,
          rXTemp - defaultStatesWidth / 2, rYTemp
              - defaultStatesHeight / 2,
          rXTemp + defaultStatesWidth / 2, rYTemp + defaultStatesHeight / 2,
          createSCounter, currPage,
          defaultStatesColor, grid, gridS);
      createSCounter++;
      objList.add(state);
      state.updateAttrib(global_attributes);
      new StateEditorWindow(fizzim_gui, this, state);
    } else if (input == locale.getString("drawArea_new_state_transition")) {
      Vector<StateObj> stateObjs = new Vector<StateObj>();
      for (GeneralObj obj : objList) {

        if (obj.getType() == GeneralObjType.STATE) {
          stateObjs.add((StateObj) obj);
        }

      }
      if (stateObjs.size() > 1) {
        GeneralObj trans = new StateTransitionObj(global_attributes,
            createTCounter, currPage,
            this, defaultStateTransitionsColor);
        createTCounter++;
        objList.add(trans);
        trans.updateAttrib(global_attributes);
        new TransitionEditorWindow(fizzim_gui, this, (TransitionObj) trans,
            stateObjs, false, null);
      } else {
        JOptionPane.showMessageDialog(this,
            "Must be more than 2 states before a transition can be created",
            "error",
            JOptionPane.ERROR_MESSAGE);
      }
    } else if (input == locale.getString("drawArea_add_loopback_transition")) {
      Vector<StateObj> stateObjs = new Vector<StateObj>();
      for (GeneralObj obj : objList) {

        if (obj.getType() == GeneralObjType.STATE) {
          stateObjs.add((StateObj) obj);
        }

      }
      if (stateObjs.size() > 0) {
        GeneralObj trans = new LoopbackTransitionObj(global_attributes,
            rXTemp, rYTemp,
            createTCounter, currPage, defaultTransitionsColor);
        createTCounter++;
        objList.add(trans);
        trans.updateAttrib(global_attributes);
        new TransitionEditorWindow(fizzim_gui, this, (TransitionObj) trans,
            stateObjs, true, (StateObj) tempObj);
      } else {
        JOptionPane
            .showMessageDialog(
                this,
                "Must be more than 1 states before a loopback transition can be created",
                "error",
                JOptionPane.ERROR_MESSAGE);
      }
    } else if (input == locale.getString("drawArea_new_loopback_transition")) {
      Vector<StateObj> stateObjs = new Vector<StateObj>();
      for (GeneralObj obj : objList) {

        if (obj.getType() == GeneralObjType.STATE) {
          stateObjs.add((StateObj) obj);
        }

      }
      if (stateObjs.size() > 0) {
        GeneralObj trans = new LoopbackTransitionObj(global_attributes,
            rXTemp, rYTemp,
            createTCounter, currPage, defaultTransitionsColor);
        createTCounter++;
        objList.add(trans);
        trans.updateAttrib(global_attributes);

        new TransitionEditorWindow(fizzim_gui, this, (TransitionObj) trans,
            stateObjs, true, null);
      } else {
        JOptionPane
            .showMessageDialog(
                this,
                "Must be more than 1 states before a loopback transition can be created",
                "error",
                JOptionPane.ERROR_MESSAGE);
      }
    } else if (input == locale.getString("drawArea_new_free_text")) {
      GeneralObj text = new TextObj(global_attributes, "", rXTemp, rYTemp,
          currPage);
      objList.add(text);
      editText((TextObj) text);
    } else if (checkStateNames(input)) {
      GeneralObj trans = new StateTransitionObj(global_attributes,
          createTCounter, currPage, this,
          (StateObj) tempObj, getStateObj(input), defaultStateTransitionsColor);
      createTCounter++;
      objList.add(trans);
      StateTransitionObj sTrans = (StateTransitionObj) trans;
      sTrans.initTrans((StateObj) tempObj, getStateObj(input));

      trans.updateAttrib(global_attributes);
      commitUndo();
    } else if (getPageIndex(input) > -1) {

      int page = getPageIndex(input);

      if (page != currPage) {
        if (!objsSelected) {
          tempObj.setPage(page);

          for (GeneralObj obj : objList) {
            if (obj.getType() != GeneralObjType.STATE && obj.isParentModified())
              obj.updateObj();
            obj.setParentModified(false);
          }
          tempObj.setModified(false);
          tempObj.updateObj();
          commitUndo();
          unselectObjs();
        } else {
          // move all states
          for (GeneralObj obj : selectedObjects) {
            if (obj.getType() == GeneralObjType.STATE
                || obj.getType() == GeneralObjType.TEXT) {
              obj.setPage(page);
              if (obj.getType() == GeneralObjType.STATE)
                obj.setModified(true);
            }
          }
          for (GeneralObj obj : objList) {
            if (obj.getType() != GeneralObjType.STATE && obj.isParentModified()) {
              TransitionObj trans = (TransitionObj) obj;
              trans.updateObjPages(page);
            }
            obj.setParentModified(false);
          }
          for (Object object_bis : selectedObjects) {
            GeneralObj obj = (GeneralObj) object_bis;
            if (obj.getType() == GeneralObjType.STATE) {
              obj.setModified(false);
            }
          }

          unselectObjs();
          objsSelected = false;
          multipleSelect = false;
          selectedObjects.clear();
          commitUndo();

        }

      }
    }
    repaint();

  }

  private int getPageIndex(String input) {
    FizzimGui fgui = (FizzimGui) fizzim_gui;
    return fgui.getPageIndex(input);
  }

  private StateObj getStateObj(String name) {
    for (GeneralObj object : objList) {
      if (object.getType() == GeneralObjType.STATE
          && object.getName().equals(name))
        return (StateObj) object;
    }
    return null;
  }

  // update state attribute lists when global list is updated
  public void updateStates() {
    String resetName = null;
    for (int j = 0; j < global_attributes.getMachineAttributes().size(); j++) {
      if (global_attributes.getMachineAttributes().get(j).getName().equals(
          "reset_state"))
        resetName = global_attributes.getMachineAttributes().get(j).getValue();
    }
    for (GeneralObj o : objList) {
      if (o.getType() == GeneralObjType.STATE) {
        StateObj s = (StateObj) o;
        s.updateAttrib(global_attributes);
        if (s.getName().equals(resetName))
          s.setReset(true);
        else
          s.setReset(false);
      }
    }

  }

  // update transition attribute lists when global list is updated
  public void updateTrans() {
    for (GeneralObj object : objList) {
      if (object.getType() == GeneralObjType.TRANSITION
          || object.getType() == GeneralObjType.LOOPBACK_TRANSITION) {
        TransitionObj s = (TransitionObj) object;
        s.updateAttrib(global_attributes);
      }
    }

  }

  public void editText(TextObj obj) {
    // ColorChooserIcon icon = new
    // ColorChooserIcon(obj.getColor(),colorChooser);
    // addMouseListener(icon);
    String s = (String) JOptionPane.showInputDialog(
        fizzim_gui,
        "Edit Text:\n",
        "Edit Text Properties",
        JOptionPane.PLAIN_MESSAGE,
        null,
        null,
        obj.getText());

    if (s != null) {
      obj.setText(s);
      commitUndo();
    }
  }

  public void setParent(FizzimGui fizzimGui) {
    fizzim_gui = fizzimGui;
  }

  /**
   * Check for duplicate names
   * 
   * @return true if there is no duplicates
   * @details
   *          TODO: can be optimized :
   *          if (!stateSet.add(obj.getName())
   *          return false;
   */
  public boolean checkStateNames() {
    TreeSet<String> stateSet = new TreeSet<String>();
    int stateCounter = 0;
    for (GeneralObj obj : objList) {
      if (obj.getType() == GeneralObjType.STATE) {
        stateSet.add(obj.getName());
        stateCounter++;
      }
    }
    if (stateSet.size() == stateCounter)
      return true;
    else
      return false;
  }

  /**
   * 
   * @param input
   * @return true if an object is already named `input`.
   */
  private boolean checkStateNames(String input) {
    for (GeneralObj object : objList) {
      if (object.getType() == GeneralObjType.STATE
          && object.getName().equals(input))
        return true;
    }
    return false;
  }

  // check for duplicate names
  public boolean checkTransNames() {
    TreeSet<String> transSet = new TreeSet<String>();
    int transCounter = 0;
    for (GeneralObj obj : objList) {
      if (obj.getType() == GeneralObjType.TRANSITION
          || obj.getType() == GeneralObjType.LOOPBACK_TRANSITION) {
        transSet.add(obj.getName());
        transCounter++;
      }
    }
    if (transSet.size() == transCounter)
      return true;
    else
      return false;
  }

  public void save(BufferedWriter writer) throws IOException {
    writer.write("## START PREFERENCES\n");
    writer.write("<SCounter>\n" + createSCounter + "\n</SCounter>\n");
    writer.write("<TCounter>\n" + createTCounter + "\n</TCounter>\n");
    writer.write("<TableVis>\n" + tableVis + "\n</TableVis>\n");
    writer.write("<TableSpace>\n" + space + "\n</TableSpace>\n");
    writer.write("<TableFont>\n" + tableFont.getFontName() + "\n"
        + tableFont.getSize() + "\n</TableFont>\n");
    writer.write("<TableColor>\n" + tableColor.getRGB() + "\n</TableColor>\n");
    writer.write("<Font>\n" + currFont.getFontName() + "\n"
        + currFont.getSize() + "\n</Font>\n");
    writer.write("<Grid>\n" + grid + "\n" + gridS + "\n</Grid>\n");
    writer.write("<PageSizeW>\n" + getMaxW() + "\n</PageSizeW>\n");
    writer.write("<PageSizeH>\n" + getMaxH() + "\n</PageSizeH>\n");
    writer.write("<StateW>\n" + getStateW() + "\n</StateW>\n");
    writer.write("<StateH>\n" + getStateH() + "\n</StateH>\n");
    writer.write("<LineWidth>\n" + getLineWidth() + "\n</LineWidth>\n");
    writer.write("## END PREFERENCES\n");
    writer.write("## START OBJECTS\n");

    // tell every object to save itself
    for (GeneralObj obj : objList) {
      obj.save(writer);
    }
    writer.write("## END OBJECTS\n");

  }

  /**
   * @brief
   *        Executed we do the delete action.
   * 
   * @details
   *          It deletes the objects (transitions and states) that are selected.
   *          When deleting a state, we also delete all the transitions going or
   *          leaving this state.
   */
  public void delete() {

    setUndoPoint(null);

    /** Transitions to delete */
    Set<TransitionObj> transitions_to_delete = new LinkedHashSet<>();

    // find indices of all transitions to delete
    Iterator<GeneralObj> general_obj_iterator = objList.iterator();
    while (general_obj_iterator.hasNext()) {
      GeneralObj obj = general_obj_iterator.next();
      // for deleting multiple selected objects, find transitions

      /**
       * If the current object is a transition, we delete it if it is connected
       * to a selected state.
       */
      if ((obj.getType() == GeneralObjType.TRANSITION || obj.getType() == GeneralObjType.LOOPBACK_TRANSITION)
          && objsSelected) {

        for (GeneralObj state : selectedObjects) {
          if (obj.containsParent(state)) {
            transitions_to_delete.add((TransitionObj) obj);
            break;
          }
        }
      }
      // make sure global table isnt removed
      if (obj.getType() == GeneralObjType.TEXT && objsSelected) {
        TextObj txt = (TextObj) obj;
        if (txt.getGlobalTable() && txt.getSelectStatus() != SelectOptions.NONE) {
          System.err.println("First : " + txt.getGlobalTable());
          System.err
              .println("Second : "
                  + (txt.getSelectStatus() != SelectOptions.NONE));
          String error = "To remove global table, go to 'File->Preferences'";
          JOptionPane.showMessageDialog(fizzim_gui,
              error,
              "error",
              JOptionPane.ERROR_MESSAGE);
        }
        // remove from selected indices

        /**
         * We remove from the selection the object we are dealing with (which is
         * a text).
         */
        for (Object object_bis : selectedObjects) {
          if (object_bis.equals(obj)) {
            selectedObjects.remove(object_bis);
            break;
          }
        }
      }

      // for deleting single object, if it is a state remove the transitions
      if (!objsSelected && obj.getSelectStatus() != SelectOptions.NONE) {
        /** If the object is selected and is a transition, we simply remove it */
        if ((obj.getType() == GeneralObjType.TRANSITION || obj.getType() == GeneralObjType.LOOPBACK_TRANSITION)) {
          general_obj_iterator.remove();
          commitUndo();
          break;
        }
        // stop delete of global table, otherwise delete

        if (obj.getType() == GeneralObjType.TEXT) {
          TextObj txt = (TextObj) obj;
          if (txt.getGlobalTable()
              && txt.getSelectStatus() != SelectOptions.NONE) {
            String error = "To remove global table, go to 'File->Preferences'";
            JOptionPane.showMessageDialog(fizzim_gui,
                error,
                "error",
                JOptionPane.ERROR_MESSAGE);
          } else {
            general_obj_iterator.remove();
            commitUndo();
            break;
          }
        }

        /*
         * The current object is a state and is selected. Then we must delete
         * all the transitions connected to this state
         */
        // if state, add transitions to delete
        if (obj.getType() == GeneralObjType.STATE) {
          for (GeneralObj t : objList) {
            if (t.getType() == GeneralObjType.TRANSITION
                || t.getType() == GeneralObjType.LOOPBACK_TRANSITION) {
              TransitionObj transition = (TransitionObj) t;
              if (transition.containsParent(obj))
                transitions_to_delete.add(transition);
            }
          }
          // make sure state gets deleted at correct time
          selectedObjects.add(obj);
        }
      }

    }
    /**
     * We delete all the objects that are selected and the transitions that must
     * be deleted because we delete its state(s).
     */
    // delete all selected
    Iterator<GeneralObj> object_iterator = selectedObjects.iterator();
    while (object_iterator.hasNext()) {
      GeneralObj object = object_iterator.next();
      object_iterator.remove();
      objList.remove(object);
    }
    Iterator<TransitionObj> transitions_iterator = transitions_to_delete
        .iterator();
    while (transitions_iterator.hasNext()) {
      TransitionObj transition = transitions_iterator.next();
      transitions_iterator.remove();
      objList.remove(transition);
    }
    /*
     * OLD CODE. Replaced
     * while (selectedObjects.size() > 0 || trans.size() > 0) {
     * int i1 = -1;
     * int i2 = -1;
     * if (selectedObjects.size() > 0)
     * i1 = objList.indexOf(arg0)
     * i1 = selectedObjects.get((selectedObjects.size() - 1)).intValue();
     * if (trans.size() > 0)
     * i2 = trans.get((trans.size() - 1)).intValue();
     * if (i1 > i2) {
     * objList.remove(i1);
     * selectedObjects.removeLast();
     * } else {
     * objList.remove(i2);
     * trans.removeLast();
     * }
     * }
     */

    commitUndo();
    objsSelected = false;
  }

  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }

  public int print(Graphics g, PageFormat pageFormat, int pageIndex)
      throws PrinterException {
    if (pageIndex > 0) {
      return (NO_SUCH_PAGE);
    } else {
      for (GeneralObj s : objList) {
        s.unselect();
      }
      Graphics2D g2d = (Graphics2D) g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      g2d.scale(pageFormat.getImageableWidth() / this.getWidth(), pageFormat
          .getImageableHeight()
          / this.getHeight());

      disableDoubleBuffering((Component) this);
      this.paint(g2d);
      enableDoubleBuffering((Component) this);
      return (PAGE_EXISTS);
    }
  }

  public void open(GlobalAttributes global_attributes,
      LinkedList<GeneralObj> objList2) {
    loading = true;
    currPage = 1;
    objList = objList2;
    undoList.clear();
    tempList.clear();
    currUndoIndex = 0;
    undoList.add(objList);
    toCommit = false;
    tempObj = null;
    tempOld = null;
    tempClone = null;
    fileModified = false;
    for (GeneralObj s : objList) {
      if (s.getType() == GeneralObjType.TRANSITION
          || s.getType() == GeneralObjType.LOOPBACK_TRANSITION) {
        TransitionObj t = (TransitionObj) s;
        t.makeConnections(objList);
      }
      if (s.getType() == GeneralObjType.TEXT) {
        TextObj textObj = (TextObj) s;
        if (textObj.getGlobalTable()) {
          textObj.loadGlobalTable(tableFont);
        }
      }
    }
    updateStates();
    updateTrans(); // Added by pz, but no sure why (initial paint of flags is
    // incorrect without it)
    setGrid(grid, gridS);
    repaint();

  }

  public void open(GlobalAttributes globals) {
    loading = true;
    currPage = 1;
    global_attributes = globals;
    objList.clear();

    TextObj globalTable = new TextObj(10, 10, global_attributes, tableFont);
    objList.add(globalTable);

    undoList.clear();
    undoList.add(objList);
    tempList.clear();
    currUndoIndex = 0;
    toCommit = false;
    tempObj = null;
    tempOld = null;
    tempClone = null;
    fileModified = false;
    createSCounter = 0;
    createTCounter = 0;
    updateStates();
    updateTrans(); // Added by pz, but no sure why (initial paint of flags is
    // incorrect without it)
    repaint();

  }

  public void setStateCounter(String readLine) {
    createSCounter = Integer.parseInt(readLine);

  }

  public void setTCounter(String readLine) {
    createTCounter = Integer.parseInt(readLine);

  }

  public void updateGlobal(GlobalAttributes globalList2) {
    global_attributes = globalList2;
  }

  public GlobalAttributes getGlobalList() {
    return global_attributes;
  }

  public void setFileModifed(boolean b) {
    fileModified = b;
  }

  public boolean getFileModifed() {
    return fileModified;
  }

  public String[] getStateNames() {
    ArrayList<String> names = new ArrayList<String>();
    names.add("null");
    for (GeneralObj s : objList) {
      if (s.getType() == GeneralObjType.STATE)
        names.add(s.getName());
    }
    String names1[] = names.toArray(new String[names.size()]);
    return names1;
  }

  public void setCurrPage(int i) {
    currPage = i;
  }

  public int getMaxH() {
    FizzimGui fgui = (FizzimGui) fizzim_gui;
    return fgui.getMaxH();
  }

  public int getMaxW() {
    FizzimGui fgui = (FizzimGui) fizzim_gui;
    return fgui.getMaxW();
  }

  public void removePage(int tab) {
    Iterator<GeneralObj> general_obj_iterator = objList.iterator();
    while (general_obj_iterator.hasNext()) {
      GeneralObj obj = general_obj_iterator.next();

      if (obj.getType() != GeneralObjType.TRANSITION) {
        if (obj.getPage() == tab) {
          general_obj_iterator.remove();
        }
        if (obj.getPage() > tab)
          obj.decrementPage();
      } else {
        StateTransitionObj obj1 = (StateTransitionObj) obj;
        if (obj1.getSPage() == tab || obj1.getEPage() == tab)
          general_obj_iterator.remove();
        if (obj1.getSPage() > tab)
          obj1.decrementSPage();
        if (obj1.getEPage() > tab)
          obj1.decrementEPage();
      }

    }

  }

  public void unselectObjs() {
    for (GeneralObj s : objList) {
      s.unselect();
    }
  }

  public void resetUndo() {
    undoList.clear();
    undoList.add(objList);
    tempList.clear();
    currUndoIndex = 0;
  }

  public String getPageName(int page) {
    FizzimGui fgui = (FizzimGui) fizzim_gui;
    return fgui.getPageName(page);
  }

  public void updateGlobalTable() {
    for (GeneralObj obj : objList) {
      if (obj.getType() == GeneralObjType.TEXT) {
        TextObj textObj = (TextObj) obj;
        if (textObj.getGlobalTable())
          textObj.updateGlobalText(global_attributes, tableFont, tableVis,
              space,
              tableColor);
      }
    }
    repaint();

  }

  public void setFont(Font font) {
    currFont = font;
  }

  public Font getFont() {
    return currFont;
  }

  public void setTableFont(Font font) {
    tableFont = font;

  }

  public Font getTableFont() {
    return tableFont;
  }

  public void setSpace(int i) {
    space = i;
  }

  public int getSpace() {
    return space;
  }

  public boolean getTableVis() {
    return tableVis;
  }

  public void setTableVis(boolean b) {
    tableVis = b;
  }

  public Color getTableColor() {
    return tableColor;
  }

  public void setTableColor(Color c) {
    tableColor = c;
  }

  public void setGrid(boolean b, int i) {
    grid = b;
    gridS = i;
    for (GeneralObj obj : objList) {
      if (obj.getType() == GeneralObjType.STATE) {
        StateObj obj1 = (StateObj) obj;
        obj1.setGrid(b, i);
      }
    }
  }

  public boolean getGrid() {
    return grid;
  }

  public int getGridSpace() {
    return gridS;
  }

  // generate pixel offset for page connectors
  public int getOffset(int page, StateObj startState,
      StateTransitionObj transObj, String type) {
    int totalNumb = 0;
    int numb = 0;
    for (GeneralObj obj : objList) {
      // find number that transition is out of total number
      if (obj.getType() == GeneralObjType.TRANSITION) {
        StateTransitionObj trans = (StateTransitionObj) obj;
        if (trans.pageConnectorExists(page, startState, type)) {
          totalNumb++;
          if (trans.equals(transObj))
            numb = totalNumb;

        }
      }
    }
    // find number for cener position
    int avg;
    if (totalNumb % 2 != 0)
      avg = (int) ((totalNumb + 1) / 2);
    else
      avg = (int) (totalNumb / 2);
    int finalOffset = (numb - avg) * 40;
    return finalOffset;
  }

  public void pageConnUpdate(StateObj startState, StateObj endState) {
    for (GeneralObj obj : objList) {
      if (obj.getType() == GeneralObjType.TRANSITION) {
        StateTransitionObj trans = (StateTransitionObj) obj;
        if (trans
            .pageConnectorExists(startState.getPage(), startState, "start")
            ||
            trans.pageConnectorExists(endState.getPage(), endState, "end")) {
          trans.setEndPts();

        }
      }
    }
  }

  // for redrawing all page connectors when page is resized
  public void updatePageConn() {
    for (GeneralObj obj : objList) {
      if (obj.getType() == GeneralObjType.TRANSITION) {
        StateTransitionObj trans = (StateTransitionObj) obj;
        if (trans.getSPage() != trans.getEPage())
          trans.setEndPts();
      }
    }
  }

  public void moveOnResize(int maxW, int maxH) {
    for (GeneralObj obj : objList) {
      // move text objects onto page
      if (obj.getType() == GeneralObjType.TEXT) {
        TextObj text = (TextObj) obj;
        text.moveIfNeeded(maxW, maxH);
      }
      if (obj.getType() == GeneralObjType.STATE) {
        StateObj state = (StateObj) obj;
        state.moveIfNeeded(maxW, maxH);
      }
    }
  }

  public Color getDefSC() {
    return defaultStatesColor;
  }

  public void setDefSC(Color defSC) {
    this.defaultStatesColor = defSC;
  }

  public Color getDefSTC() {
    return defaultStateTransitionsColor;
  }

  public void setDefSTC(Color defSTC) {
    this.defaultStateTransitionsColor = defSTC;
  }

  public Color getDefLTC() {
    return defaultTransitionsColor;
  }

  public void setDefLTC(Color defLTC) {
    this.defaultTransitionsColor = defLTC;
  }

  public JColorChooser getColorChooser() {
    return colorChooser;
  }

  public int getStateW() {
    return defaultStatesWidth;
  }

  public int getStateH() {
    return defaultStatesHeight;
  }

  public int getLineWidth() {
    return LineWidth;
  }

  public void setStateW(int w) {
    defaultStatesWidth = w;
  }

  public void setStateH(int h) {
    defaultStatesHeight = h;
  }

  public void setLineWidth(int w) {
    LineWidth = w;
  }

  public boolean getRedraw() {
    return Redraw;
  }

}