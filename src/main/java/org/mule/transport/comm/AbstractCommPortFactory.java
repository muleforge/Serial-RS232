/*
 * Generated by the Mule project wizard. http://mule.mulesource.org
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.comm;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.MapUtils;

import javax.comm.CommPort;
import javax.comm.NoSuchPortException;
import javax.comm.UnsupportedCommOperationException;
import javax.comm.PortInUseException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Christopher Cheng
 * Date: Mar 1, 2009
 * Time: 2:34:28 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCommPortFactory implements KeyedPoolableObjectFactory
{

    /**
     * logger used by this class
     */
    private static final Log logger = LogFactory.getLog(CommPortFactory.class);

    public Object makeObject(Object key) throws Exception
    {
        CommPortKey commPortKey = (CommPortKey) key;

        CommPort port = createPort(commPortKey);
        // todo configure comm properties
//        port.setReuseAddress(true);

        CommConnector connector = commPortKey.getConnector();
        connector.configurePort(CommConnector.CLIENT, port);

        return port;
    }

    protected abstract CommPort createPort(CommPortKey key) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException;

    public void destroyObject(Object key, Object object) throws Exception
    {
        CommPort commPort = (CommPort) object;
//        if(!commPort.isClosed())
//        {
            commPort.close();
//        }
    }

    public boolean validateObject(Object key, Object object)
    {
        CommPort commPort = (CommPort) object;
        // todo is there validate port
//        return !commPort.isClosed();
        return true;
    }

    public void activateObject(Object key, Object object) throws Exception
    {
        // cannot really activate a Socket
    }

    public void passivateObject(Object key, Object object) throws Exception
    {
        CommPortKey portKey = (CommPortKey) key;

        boolean keepSocketOpen = MapUtils.getBooleanValue(portKey.getEndpoint().getProperties(),
            CommConnector.KEEP_SEND_SOCKET_OPEN_PROPERTY, portKey.getConnector().isKeepSendSocketOpen());
        CommPort commPort = (CommPort) object;

        if (!keepSocketOpen)
        {
            try
            {
                if (commPort != null)
                {
                    commPort.close();
                }
            }
            catch (Exception e)
            {
                logger.debug("Failed to close socket after dispatch: " + e.getMessage());
            }
        }
    }

}