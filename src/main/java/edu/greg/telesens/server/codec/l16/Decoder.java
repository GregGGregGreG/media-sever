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
package edu.greg.telesens.server.codec.l16;


import edu.greg.telesens.server.format.Format;
import edu.greg.telesens.server.format.FormatFactory;
import edu.greg.telesens.server.memory.ByteFrame;
import edu.greg.telesens.server.memory.ShortFrame;
import edu.greg.telesens.server.memory.ShortMemory;

/**
 * @author oifa yulian
 */
public class Decoder {

    private final static Format linear = FormatFactory.createAudioFormat("linear", 8000, 16, 1);

    private int i, j;
    private byte[] data;
    private short[] resData;

    public ShortFrame process(ByteFrame frame) {
        data = frame.getData();
        ShortFrame res = ShortMemory.allocate(data.length / 2);
        resData = res.getData();

        for (i = 0, j = 0; i < data.length; i += 2)
            resData[j++] = (short) (((data[i + 1]) << 8) | (data[i] & 0xff));

        res.setOffset(0);
        res.setLength(resData.length);
        res.setTimestamp(frame.getTimestamp());
        res.setDuration(frame.getDuration());
        res.setSequenceNumber(frame.getSequenceNumber());
        res.setEOM(frame.isEOM());
        res.setFormat(linear);
        return res;
    }
}
