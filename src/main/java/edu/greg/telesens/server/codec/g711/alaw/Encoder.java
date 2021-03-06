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
package edu.greg.telesens.server.codec.g711.alaw;


import edu.greg.telesens.server.format.Format;
import edu.greg.telesens.server.format.FormatFactory;
import edu.greg.telesens.server.memory.ByteFrame;
import edu.greg.telesens.server.memory.ByteMemory;
import edu.greg.telesens.server.memory.ShortFrame;

/**
 * Implements G.711 A-law compressor.
 *
 * @author Yulian Oifa
 */
public class Encoder {

    private final static Format alaw = FormatFactory.createAudioFormat("pcma", 8000, 8, 1);

    private int i, count;

    /**
     * (Non Java-doc)
     *
     * @see ua.mobius.media.server.impl.jmf.dsp.Codec#process(Buffer).
     */
    public ByteFrame process(ShortFrame frame) {
        count = frame.getLength();
        ByteFrame res = ByteMemory.allocate(count);

        short[] data = frame.getData();
        byte[] resData = res.getData();

        for (i = 0; i < count; i++)
            resData[i] = EncoderData.aLawCompressTable[data[i] & 0XFFFF];

        res.setOffset(0);
        res.setLength(count);
        res.setFormat(alaw);
        res.setTimestamp(frame.getTimestamp());
        res.setDuration(frame.getDuration());
        res.setEOM(frame.isEOM());
        res.setSequenceNumber(frame.getSequenceNumber());

        return res;
    }
}