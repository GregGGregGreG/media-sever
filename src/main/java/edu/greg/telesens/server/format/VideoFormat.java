/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * 15/07/13 - Change notice:
 * This file has been modified by Mobius Software Ltd.
 * For more information please visit http://www.mobius.ua
 */
package edu.greg.telesens.server.format;

/**
 * @author kulikov
 */
public class VideoFormat extends Format implements Cloneable {
    //number of frames per second
    private int frameRate;

    /**
     * Creates new format descriptor.
     *
     * @param name format encoding name.
     */
    protected VideoFormat(String name) {
        super(name);
    }

    /**
     * Creates new format descriptor.
     *
     * @param name      format encoding name.
     * @param frameRate the number of frames per second.
     */
    protected VideoFormat(String name, int frameRate) {
        super(name);
        this.frameRate = frameRate;
    }


    /**
     * Gets the frame rate.
     *
     * @return the number of frames per second.
     */
    public int getFrameRate() {
        return frameRate;
    }

    /**
     * Modifies frame rate.
     *
     * @param frameRate the new value in frames per second.
     */
    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }

    @Override
    public VideoFormat clone() {
        return new VideoFormat(getName(), frameRate);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AudioFormat[");
        builder.append(getName().toString());

        builder.append(",");
        builder.append(frameRate);

        builder.append("]");
        return builder.toString();
    }

}
