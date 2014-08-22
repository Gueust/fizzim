package entities;

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
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

import attributes.GlobalAttributes;
import attributes.ObjAttribute;

public class StateObj extends GeneralObj implements Cloneable {

  int x0, y0, x1, y1, xTemp, yTemp, temp;
  // set enumerations for selection status
  private SelectOptions selectStatus = SelectOptions.NONE;
  private boolean reset = false;

  // grid
  private boolean grid = false;
  private int gridS = 25;

  private Color color = Color.black;

  // AttributeList mylist = new AttributeList();

  /**
   * @details The coordinates are the ones of the rectangle containing the state
   * @param The
   *          global attributes.
   * @param _x0
   *          Horizontal initial coordinate of the upper left point
   * @param _y0
   *          Vertical initial coordinate of the upper left point
   * @param _x1
   *          Horizontal initial coordinate of the lower right point
   * @param _y1
   *          Vertical initial coordinate of the lower right point
   * @param numb
   *          This number will be in the name of the state (i.e.
   *          stage+`numb`)
   * @param page
   *          The tab in which the state is added
   * @param border_color
   *          The color of the borders
   * @param b
   *          ??
   * @param i
   *          ??
   */
  public StateObj(GlobalAttributes globals,
      int _x0, int _y0, int _x1, int _y1, int numb, int page,
      Color border_color, boolean b, int i) {
    super("state" + numb, globals);
    x0 = _x0;
    y0 = _y0;
    x1 = _x1;
    y1 = _y1;
    selectStatus = SelectOptions.NONE;
    color = border_color;
    myPage = page;

    grid = b;
    gridS = i;

  }

  public StateObj(GlobalAttributes globals, int x02, int y02, int x12, int y12,
      LinkedList<ObjAttribute> newList, String name, boolean b, int page,
      Color c) {
    super(name, globals);
    x0 = x02;
    y0 = y02;
    x1 = x12;
    y1 = y12;
    attrib = newList;
    reset = b;
    selectStatus = SelectOptions.NONE;
    myPage = page;
    color = c;
  }

  @SuppressWarnings("unchecked")
  public Object clone() throws CloneNotSupportedException {
    StateObj copy = (StateObj) super.clone();
    if (attrib != null) {
      copy.attrib = (LinkedList<ObjAttribute>) copy.attrib.clone();
      for (int i = 0; i < attrib.size(); i++) {
        copy.attrib.set(i, (ObjAttribute) attrib.get(i).clone());
      }
    }

    return copy;
  }

  @Override
  public void paintComponent(Graphics g) {
    if (myPage == currPage) {
      g.setColor(color);
      // this is safe because resizeObj makes sure y1 > y0 and x1 > x0
      g.drawOval(x0, y0, x1 - x0, y1 - y0);
      if (reset)
        g.drawOval(x0 - 3, y0 - 3, x1 - x0 + 6, y1 - y0 + 6);

      /*
       * When the state is selected:
       * - the display is turned red
       * - display the rectangle around the name
       * - display the angle control points
       */
      if (selectStatus != SelectOptions.NONE) {
        g.setColor(Color.red);
        // Name
        g.drawRect(x0, y0, x1 - x0, y1 - y0);
        // Angle Control points
        g.fillRect(x0 - 3, y0 - 3, 7, 7);
        g.fillRect(x1 - 3, y0 - 3, 7, 7);
        g.fillRect(x0 - 3, y1 - 3, 7, 7);
        g.fillRect(x1 - 3, y1 - 3, 7, 7);
      }

    }
  }

  @Override
  public boolean setSelectStatus(int x, int y) {
    if (myPage == currPage) {
      xTemp = x;
      yTemp = y;
      selectStatus = SelectOptions.NONE;
      // check text objects
      if (attrib != null) {
        for (int j = 0; j < attrib.size(); j++) {
          ObjAttribute s = attrib.get(j);
          s.unselect();
        }
        for (int i = 0; i < attrib.size(); i++) {
          ObjAttribute s = attrib.get(i);
          if (s.setSelectStatus(x, y)) {
            selectStatus = SelectOptions.TXT;
            break;
          }
        }
      }

      if (selectStatus != SelectOptions.TXT) {
        // check if inside square
        if (x >= x0 && x <= x1 && y >= y0 && y <= y1)
          selectStatus = SelectOptions.CENTER;
        // check corners
        if (x0 - x <= 3 && x0 - x >= -3 && y0 - y <= 3 && y0 - y >= -3)
          selectStatus = SelectOptions.TL;
        if (x1 - x <= 3 && x1 - x >= -3 && y0 - y <= 3 && y0 - y >= -3)
          selectStatus = SelectOptions.TR;
        if (x0 - x <= 3 && x0 - x >= -3 && y1 - y <= 3 && y1 - y >= -3)
          selectStatus = SelectOptions.BL;
        if (x1 - x <= 3 && x1 - x >= -3 && y1 - y <= 3 && y1 - y >= -3)
          selectStatus = SelectOptions.BR;
      }
      if (selectStatus == SelectOptions.NONE)
        return false;
      else
        return true;
    } else
      return false;

  }

  @Override
  public void setSelectStatus(boolean b) {
    if (b)
      selectStatus = SelectOptions.CENTER;
    else
      selectStatus = SelectOptions.NONE;
  }

  @Override
  public void unselect() {
    selectStatus = SelectOptions.NONE;
  }

  @Override
  public void adjustShapeOrPosition(int x, int y) {
    if (myPage == currPage) {
      // move object stuff
      if (selectStatus == SelectOptions.CENTER) {
        if (grid) {
          x -= (x % gridS);
          y -= (y % gridS);

        }
        x0 += x - xTemp;
        y0 += y - yTemp;
        x1 += x - xTemp;
        y1 += y - yTemp;
        xTemp = x;
        yTemp = y;
        if (grid) {
          int w = x1 - x0;
          int h = y1 - y0;
          x0 -= (x0 % gridS);
          y0 -= (y0 % gridS);
          x1 = x0 + w;
          y1 = y0 + h;
        }

      } else if (selectStatus == SelectOptions.TXT) {
        if (attrib != null) {
          for (int i = 0; i < attrib.size(); i++) {
            ObjAttribute s = attrib.get(i);
            if (s.getSelectStatus() != 0) {
              s.adjustShapeOrPosition(x, y);
              break;
            }
          }
        }
      }

      // resize stuff
      else {

        // adjust corners

        if (selectStatus == SelectOptions.TL
            || selectStatus == SelectOptions.BL)
          x0 += x - xTemp;
        if (selectStatus == SelectOptions.TR
            || selectStatus == SelectOptions.BR)
          x1 += x - xTemp;
        if (selectStatus == SelectOptions.TL
            || selectStatus == SelectOptions.TR)
          y0 += y - yTemp;
        if (selectStatus == SelectOptions.BL
            || selectStatus == SelectOptions.BR)
          y1 += y - yTemp;
        xTemp = x;
        yTemp = y;
        // make sure x1 and y1 are not smaller than x0 and y0
        if (x1 <= x0)
          x1 = x0 + 5;
        if (y1 <= y0)
          y1 = y0 + 5;
      }

      // if(movingTxt <= 1)
      modified = true;
    }
  }

  @Override
  public SelectOptions getSelectStatus() {
    return selectStatus;
  }

  public int[] getCoords() {
    int[] a = { x0, y0, x1, y1 };
    return a;
  }

  public boolean containsState(StateObj oldObj) {
    return false;
  }

  @Override
  public Point getCenter(int page) {
    Point a = new Point(x0 + ((x1 - x0) / 2), y0 + ((y1 - y0) / 4));
    return a;
  }

  public int getSize() {
    int a = (x1 - x0) * (y1 - y0);
    return a;
  }

  // this provided a transition object a way to know if the state object has
  // been
  // moved or resized (because if will have to then change its endpoints)
  @Override
  public boolean isModified() {
    if (modified)
      return true;
    else
      return false;

  }

  @Override
  public GeneralObjType getType() {
    return GeneralObjType.STATE;
  }

  // creats array of points around the circle
  public Vector<Point> getBorderPts() {
    Vector<Point> borderPts = new Vector<Point>(36);
    int w = x1 - x0;
    int h = y1 - y0;
    for (int i = 0; i < 36; i++) {
      int x = (int) ((x0 + w / 2) + (w / 2)
          * Math.cos((double) (2 * Math.PI / 36) * i));
      int y = (int) ((y0 + h / 2) + (h / 2)
          * Math.sin((double) (2 * Math.PI / 36) * i));
      Point coord = new Point(x, y);
      borderPts.add(i, coord);
    }
    return borderPts;

  }

  @Override
  public void updateObj() {

  }

  public boolean isStateModified() {
    return false;
  }

  public void setStateModifiedFalse() {
  }

  /* What is the use of this ?! */
  public void setStateModifiedTrue() {
  }

  @Override
  public boolean containsParent(GeneralObj oldObj) {
    return false;
  }

  @Override
  public boolean isParentModified() {
    return false;
  }

  @Override
  public void notifyChange(GeneralObj old, GeneralObj clone) {

  }

  @Override
  public Point getStart() {
    int w = x1 - x0;
    int h = y1 - y0;
    Point pt = new Point(x0 + (int) (.5 * w), y0 + (int) (.35 * h));

    return pt;
  }

  public void setSize(int w, int h) {
    x1 = x0 + w;
    y1 = y0 + h;
  }

  public int getWidth() {
    return x1 - x0;
  }

  public int getHeight() {
    return y1 - y0;
  }

  @Override
  public void save(BufferedWriter writer) throws IOException {

    writer.write("## START STATE OBJECT\n");
    writer.write("<state>\n");

    writer.write(i(1) + "<attributes>\n");
    for (int i = 0; i < attrib.size(); i++) {
      ObjAttribute obj = attrib.get(i);
      obj.save(writer, 1);
    }
    writer.write(i(1) + "</attributes>\n");

    // x0,y0,x1,y1
    writer.write(i(1) + "<x0>\n" + i(1) + x0 + "\n" + i(1) + "</x0>\n");
    writer.write(i(1) + "<y0>\n" + i(1) + y0 + "\n" + i(1) + "</y0>\n");
    writer.write(i(1) + "<x1>\n" + i(1) + x1 + "\n" + i(1) + "</x1>\n");
    writer.write(i(1) + "<y1>\n" + i(1) + y1 + "\n" + i(1) + "</y1>\n");
    writer
        .write(i(1) + "<reset>\n" + i(1) + reset + "\n" + i(1) + "</reset>\n");
    writer.write(i(1) + "<page>\n" + i(1) + myPage + "\n" + i(1) + "</page>\n");
    writer.write(i(1) + "<color>\n" + i(1) + color.getRGB() + "\n" + i(1)
        + "</color>\n");

    writer.write("</state>\n");
    writer.write("## END STATE OBJECT\n");
  }

  public void setReset(boolean b) {
    reset = b;
  }

  public Point getRealCenter(int myPage) {
    Point a = new Point(x0 + ((x1 - x0) / 2), y0 + ((y1 - y0) / 2));
    return a;
  }

  public void setGrid(boolean b, int i) {
    grid = b;
    gridS = i;

  }

  public void moveIfNeeded(int maxW, int maxH) {
    if (x0 > maxW) {
      int w = x1 - x0;
      x0 = maxW - w;
      x1 = x0 + w;
    }
    if (y0 > maxH) {
      int h = y1 - y0;
      y0 = maxH - h;
      y1 = y0 + h;
    }
  }

  @Override
  public boolean setBoxSelectStatus(int x, int y) {
    xTemp = x;
    yTemp = y;
    if (myPage == currPage && x >= x0 && x <= x1 && y >= y0 && y <= y1)
      return true;
    else
      return false;
  }

  @Override
  public boolean setBoxSelectStatus(int _x0, int _y0, int _x1, int _y1) {
    if (myPage == currPage && x0 > _x0 && y0 > _y0 && x1 < _x1 && y1 < _y1) {
      selectStatus = SelectOptions.CENTER;
      return true;
    } else {
      selectStatus = SelectOptions.NONE;
      return false;
    }
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color c) {
    color = c;
  }

  @Override
  public LinkedList<ObjAttribute> getAttributes(GlobalAttributes globals) {
    return globals.getStateAttributes();
  }

}
