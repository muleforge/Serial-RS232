/*
 * Generated by the Mule project wizard. http://mule.mulesource.org
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.comm.protocols;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Christopher Cheng
 * Date: Mar 1, 2009
 * Time: 3:12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectProtocol extends AbstractByteProtocol
{

    protected static final int UNLIMITED = -1;

    private static final Log logger = LogFactory.getLog(DirectProtocol.class);
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    protected int bufferSize;

    public DirectProtocol()
    {
        this(STREAM_OK, DEFAULT_BUFFER_SIZE);
    }

    public DirectProtocol(boolean streamOk, int bufferSize)
    {
        super(streamOk);
        this.bufferSize = bufferSize;
    }

    public Object read(InputStream is) throws IOException
    {
        return read(is, UNLIMITED);
    }

    public Object read(InputStream is, int limit) throws IOException
    {
        // this can grow on repeated reads
        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);

        try
        {
            byte[] buffer = new byte[bufferSize];
            int len;
            int remain = remaining(limit, limit, 0);
            boolean repeat;
            do
            {

                len = copy(is, buffer, baos, remain);
                remain = remaining(limit, remain, len);
                repeat = EOF != len && remain > 0 && isRepeat(len, is.available());

                if (logger.isDebugEnabled())
                {
                    logger.debug(MessageFormat.format(
                            "len/limit/repeat: {0}/{1}/{2}",
                            new Object[] {new Integer(len), new Integer(limit), Boolean.valueOf(repeat)}));
                }
            }
            while (repeat);
        }
        finally
        {
            baos.flush();
            baos.close();
        }
        return nullEmptyArray(baos.toByteArray());
        */

        // Loop until state is set to 0
        String s = "";

        // Read buffer until Char(3) is received
        byte b = 0;
        boolean start = false;
        while (b != 3) {
            int n = is.read();
            b = (byte) n;
            if (n != -1) {
                // System.out.println((char) n);
            }
            if (b == 2) {
                s = "";
                start = true;
            }
            if (start && b != -1 && b != 2 && b != 3) {
                s += (char) b;
            }
        }

        return s;
    }

    protected int remaining(int limit, int remain, int len)
    {
        if (UNLIMITED == limit)
        {
            return bufferSize;
        }
        else if (EOF != len)
        {
            return remain - len;
        }
        else
        {
            return remain;
        }
    }

    /**
     * Decide whether to repeat transfer.  This implementation does so if
     * more data are available.  Note that previously, while documented as such,
     * there was also the additional requirement that the previous transfer
     * completely used the transfer buffer.
     *
     * @param len Amount transferred last call (-1 on EOF or socket error)
     * @param available Amount available
     * @return true if the transfer should continue
     */
    protected boolean isRepeat(int len, int available)
    {
        // previous logic - less reliable on slow networks
//        return len == bufferSize && available > 0;

        return available > 0;
    }

}