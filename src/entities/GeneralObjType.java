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

/**
 * @brief The enumeration of the different types of the objects.
 *        To replace the previously hard-coded enumeration :
 *        The type of object :
 *        0 for StateObj
 *        1 for StateTransitionObj
 *        2 for LoopbackTransitionObj
 *        3 for TextObj
 */
public enum GeneralObjType {
  STATE,
  TRANSITION,
  LOOPBACK_TRANSITION,
  TEXT;
}
