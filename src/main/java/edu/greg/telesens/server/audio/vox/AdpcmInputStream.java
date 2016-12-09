/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package edu.greg.telesens.server.audio.vox;

import javax.sound.sampled.AudioFormat;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;


/**
 * AdpcmInputStream.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 030714 nsano initial version <br>
 *          0.01 030714 nsano fine tune <br>
 *          0.02 030714 nsano fix available() <br>
 *          0.03 030715 nsano read() endian <br>
 *          0.10 060427 nsano refactoring <br>
 */
public abstract class AdpcmInputStream extends FilterInputStream {

    protected AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;

    protected ByteOrder byteOrder;

    protected Codec decoder;

    protected abstract Codec getCodec();

    /**
     * @param in PCM
     * @param byteOrder {@link #read()}
     * @param bits {@link BitOutputStream}
     * @param bitOrder {@link BitOutputStream}
     */
    public AdpcmInputStream(InputStream in, ByteOrder byteOrder, int bits, ByteOrder bitOrder) {
        super(new BitInputStream(in, bits, bitOrder));
        this.byteOrder = byteOrder;
        this.decoder = getCodec();
    }

    public int available() throws IOException {
        return (in.available() * 2) + (rest ? 1 : 0);
    }

    protected boolean rest = false;
    protected int current;

    /**
     * @return PCM H or L (8bit LSB)
     */
    public int read() throws IOException {
        if (!rest) {
            int adpcm = in.read();
            if (adpcm == -1) {
                return -1;
            }

            current = decoder.decode(adpcm);

            rest = true;
            if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
                return (current & 0xff00) >> 8;
            } else {
                return current & 0xff;
            }
        } else {
            rest = false;
            if (ByteOrder.BIG_ENDIAN.equals(byteOrder)) {
                return current & 0xff;
            } else {
                return (current & 0xff00) >> 8;
            }
        }
    }

    /* */
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                 ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                if (b != null) {
                    b[off + i] = (byte) c;
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return i;
    }
}

