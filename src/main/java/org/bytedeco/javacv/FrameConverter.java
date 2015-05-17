/*
 * Copyright (C) 2015 Samuel Audet
 *
 * This file is part of JavaCV.
 *
 * JavaCV is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * JavaCV is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bytedeco.javacv;

/**
 * Defines two methods to convert between a {@link Frame} and another generic
 * data object that can contain the same data. The idea with this design is
 * to allow users to convert easily between multiple potentially mutually
 * exclusive types of image data objects over which we have no control. Because
 * of this, and for performance reasons, any object returned by this class is
 * guaranteed to remain valid only until the next call to {@code convert()},
 * anywhere in a chain of {@code FrameConverter} objects, and only as long as
 * the latter themselves are not garbage collected.
 *
 * @author Samuel Audet
 */
public abstract class FrameConverter<F> {
    protected Frame frame;

    public abstract Frame convert(F f);
    public abstract F convert(Frame frame);
}
