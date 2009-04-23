/*
 * Generated by the Mule project wizard. http://mule.mulesource.org
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.comm;

import org.mule.transport.AbstractConnector;
import org.mule.transport.comm.protocols.SafeProtocol;
import org.mule.transport.comm.protocols.DirectProtocol;
import org.mule.transport.tcp.AbstractTcpSocketFactory;
//import org.mule.transport.tcp.*;
//import org.mule.transport.tcp.protocols.SafeProtocol;
import org.mule.util.monitor.ExpiryMonitor;
import org.mule.api.transport.Connector;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.MuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MessagingException;
import org.mule.config.i18n.CoreMessages;
import org.mule.model.streaming.CallbackOutputStream;
import org.mule.endpoint.URIBuilder;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.validator.GenericValidator;

import javax.comm.*;
//import java.net.Socket;
//import java.net.SocketException;
//import java.net.ServerSocket;
import java.net.URI;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Christopher Cheng
 * Date: Mar 1, 2009
 * Time: 2:26:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommConnector extends AbstractConnector
{
    public static final String COMM = "comm";

    /** Property can be set on the endpoint to configure how the socket is managed */
    public static final String KEEP_SEND_SOCKET_OPEN_PROPERTY = "keepSendSocketOpen";
    public static final int DEFAULT_SOCKET_TIMEOUT = INT_VALUE_NOT_SET;
    public static final int DEFAULT_SO_LINGER = INT_VALUE_NOT_SET;
    public static final int DEFAULT_BUFFER_SIZE = INT_VALUE_NOT_SET;
    public static final int DEFAULT_BACKLOG = INT_VALUE_NOT_SET;

    // to clarify arg to configureSocket
    public static final boolean SERVER = false;
    public static final boolean CLIENT = true;

    private int clientSoTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int serverSoTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int sendBufferSize = DEFAULT_BUFFER_SIZE;
    private int receiveBufferSize = DEFAULT_BUFFER_SIZE;
    private int receiveBacklog = DEFAULT_BACKLOG;
    private boolean sendTcpNoDelay;
    private boolean validateConnections = true;
    private Boolean reuseAddress = Boolean.TRUE; // this could be null for Java default
    private int socketSoLinger = DEFAULT_SO_LINGER;
    private CommProtocol commProtocol;
    private AbstractCommPortFactory portFactory;
    private SimpleServerPortFactory serverPortFactory;
//    private GenericKeyedObjectPool portsPool = new GenericKeyedObjectPool();
    static private Map portsMap = new HashMap();
    private int keepAliveTimeout = 0;
    private ExpiryMonitor keepAliveMonitor;

    /**
     * If set, the socket is not closed after sending a message.  This attribute
     * only applies when sending data over a socket (Client).
     */
    private boolean keepSendSocketOpen = false;

    /**
     * Enables SO_KEEPALIVE behavior on open sockets. This automatically checks
     * socket connections that are open but unused for long periods and closes
     * them if the connection becomes unavailable.  This is a property on the
     * socket itself and is used by a server socket to control whether
     * connections to the server are kept alive before they are recycled.
     */
    private boolean keepAlive = false;

    //TODO MULE-2300 remove once fixed
    private CommPortKey lastSocketKey;

    public CommConnector()
    {
        try {
            setPortFactory(new CommPortFactory());
            setServerSocketFactory(new CommServerPortFactory());
            setCommProtocol(new DirectProtocol());
            keepAliveMonitor = new ExpiryMonitor("PortTimeoutMonitor", 1000);
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void configurePort(boolean client, CommPort port)
    {
        // There is some overhead in setting socket timeout and buffer size, so we're
        // careful here only to set if needed

        // todo configure Comm Port
        /*
        if (newValue(getReceiveBufferSize(), port.getReceiveBufferSize()))
        {
            port.setReceiveBufferSize(getReceiveBufferSize());
        }
        if (newValue(getSendBufferSize(), port.getSendBufferSize()))
        {
            port.setSendBufferSize(getSendBufferSize());
        }
        if (client)
        {
            if (newValue(getClientSoTimeout(), port.getSoTimeout()))
            {
                port.setSoTimeout(getClientSoTimeout());
            }
        }
        else
        {
            if (newValue(getServerSoTimeout(), port.getSoTimeout()))
            {
                port.setSoTimeout(getServerSoTimeout());
            }
        }
        if (newValue(getSocketSoLinger(), port.getSoLinger()))
        {
            port.setSoLinger(true, getSocketSoLinger());
        }
        try
        {
            port.setTcpNoDelay(isSendTcpNoDelay());
        }
        catch (SocketException e)
        {
            // MULE-2800 - Bug in Solaris
        }
        port.setKeepAlive(isKeepAlive());
        */
    }

    static public Map getPortsMap() {
        return portsMap;
    }

    private boolean newValue(int parameter, int socketValue)
    {
        return parameter != Connector.INT_VALUE_NOT_SET && parameter != socketValue;
    }

    protected void doInitialise() throws InitialisationException
    {
    }

    protected void doDispose()
    {
        logger.debug("Closing Comm connector");
        try
        {
//            portsPool.close();
        }
        catch (Exception e)
        {
            logger.warn("Failed to close dispatcher serial port: " + e.getMessage());
        }

        keepAliveMonitor.dispose();
    }

    /**
     * Lookup a socket in the list of dispatcher sockets but don't create a new
     * socket
     */
    protected CommPort getPort(ImmutableEndpoint endpoint) throws Exception
    {
        CommPortKey portKey = new CommPortKey(endpoint);
        if (logger.isDebugEnabled())
        {
            logger.debug("borrowing port for " + portKey + "/" + portKey.hashCode());
            if (null != lastSocketKey)
            {
                logger.debug("same as " + lastSocketKey.hashCode() + "? " + lastSocketKey.equals(portKey));
            }
        }
        CommPort port = (CommPort) portsMap.get(portKey.getPort());
        if (port == null) {
            CommPortIdentifier com = CommPortIdentifier.getPortIdentifier(portKey.getPort());
            System.out.println("CommConnector delay=" + 100);
            port = com.open(portKey.getPort(), 100);
            portsMap.put(portKey.getPort(), port);
            System.out.println("put " + portKey.getPort() + " into the map");
        }
        else {
            
//            System.out.println("return port from map " + portKey.getPort());
        }

//        if (logger.isDebugEnabled())
//        {
//            logger.debug("borrowed port, "
//                    + (commPort.isClosed() ? "closed" : "open")
//                    + "; debt " + portsPool.getNumActive());
//        }

        return port;
    }

    void releasePort(CommPort commPort, ImmutableEndpoint endpoint) throws Exception
    {
        commPort.getOutputStream().close();

//        CommPortKey portKey = new CommPortKey(endpoint);
//        lastSocketKey = portKey;
//        portsPool.returnObject(portKey, commPort);
        
        if (logger.isDebugEnabled())
        {
//            logger.debug("returning port for " + portKey.hashCode());
//            logger.debug("returned port; debt " + portsPool.getNumActive());
        }
//        System.out.println("releasePort called");
    }

    public OutputStream getOutputStream(final ImmutableEndpoint endpoint, MuleMessage message)
            throws MuleException
    {
        final CommPort commPort;
        try
        {
            commPort = getPort(endpoint);
        }
        catch (Exception e)
        {
            throw new MessagingException(CoreMessages.failedToGetOutputStream(), message, e);
        }
        if (commPort == null)
        {
            // This shouldn't happen
            throw new IllegalStateException("could not get port for endpoint: "
                    + endpoint.getEndpointURI().getAddress());
        }
        try
        {
            System.out.println("getOutputStream called");
            return commPort.getOutputStream();
            /*
            return new CallbackOutputStream(
                    new DataOutputStream(new BufferedOutputStream(commPort.getOutputStream())),
                    new CallbackOutputStream.Callback()
                    {
                        public void onClose() throws Exception
                        {
                            releasePort(commPort, endpoint);
                        }
                    });
                    */
        }
        catch (IOException e)
        {
            throw new MessagingException(CoreMessages.failedToGetOutputStream(), message, e);
        }
    }

    protected void doConnect() throws Exception
    {
        // template method
    }

    protected void doDisconnect() throws Exception
    {
//        portsPool.clear();
    }

    protected void doStart() throws MuleException
    {
        // template method
    }

    protected void doStop() throws MuleException
    {
        // template method
    }

    public String getProtocol()
    {
        return COMM;
    }

    // getters and setters ---------------------------------------------------------

    public boolean isKeepSendSocketOpen()
    {
        return keepSendSocketOpen;
    }

    public void setKeepSendSocketOpen(boolean keepSendSocketOpen)
    {
        this.keepSendSocketOpen = keepSendSocketOpen;
    }

    /**
     * A shorthand property setting timeout for both SEND and RECEIVE sockets.
     *
     * @deprecated The time out should be set explicitly for each
     */
    public void setTimeout(int timeout)
    {
        setClientSoTimeout(timeout);
        setServerSoTimeout(timeout);
    }

    public int getClientSoTimeout()
    {
        return this.clientSoTimeout;
    }

    public void setClientSoTimeout(int timeout)
    {
        this.clientSoTimeout = valueOrDefault(timeout, 0, DEFAULT_SOCKET_TIMEOUT);
    }

    public int getServerSoTimeout()
    {
        return serverSoTimeout;
    }

    public void setServerSoTimeout(int timeout)
    {
        this.serverSoTimeout = valueOrDefault(timeout, 0, DEFAULT_SOCKET_TIMEOUT);
    }

    /** @deprecated Should use {@link #getSendBufferSize()} or {@link #getReceiveBufferSize()} */
    public int getBufferSize()
    {
        return sendBufferSize;
    }

    /** @deprecated Should use {@link #setSendBufferSize(int)} or {@link #setReceiveBufferSize(int)} */
    public void setBufferSize(int bufferSize)
    {
        sendBufferSize = valueOrDefault(bufferSize, 1, DEFAULT_BUFFER_SIZE);
    }

    public int getSendBufferSize()
    {
        return sendBufferSize;
    }

    public void setSendBufferSize(int bufferSize)
    {
        sendBufferSize = valueOrDefault(bufferSize, 1, DEFAULT_BUFFER_SIZE);
    }

    public int getReceiveBufferSize()
    {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int bufferSize)
    {
        receiveBufferSize = valueOrDefault(bufferSize, 1, DEFAULT_BUFFER_SIZE);
    }

    public int getReceiveBacklog()
    {
        return receiveBacklog;
    }

    public void setReceiveBacklog(int receiveBacklog)
    {
        this.receiveBacklog = valueOrDefault(receiveBacklog, 0, DEFAULT_BACKLOG);
    }

    public int getSocketSoLinger()
    {
        return socketSoLinger;
    }

    public void setSocketSoLinger(int soLinger)
    {
        this.socketSoLinger = valueOrDefault(soLinger, 0, INT_VALUE_NOT_SET);
    }

    /**
     * @return
     * @deprecated should use {@link #getReceiveBacklog()}
     */
    public int getBacklog()
    {
        return receiveBacklog;
    }

    /**
     * @param backlog
     * @deprecated should use {@link #setReceiveBacklog(int)}
     */
    public void setBacklog(int backlog)
    {
        this.receiveBacklog = backlog;
    }

    public CommProtocol getCommProtocol()
    {
        return commProtocol;
    }

    public void setCommProtocol(CommProtocol commProtocol)
    {
        this.commProtocol = commProtocol;
    }

    public boolean isRemoteSyncEnabled()
    {
        return true;
    }

    public boolean isKeepAlive()
    {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive)
    {
        this.keepAlive = keepAlive;
    }

    public boolean isSendTcpNoDelay()
    {
        return sendTcpNoDelay;
    }

    public void setSendTcpNoDelay(boolean sendTcpNoDelay)
    {
        this.sendTcpNoDelay = sendTcpNoDelay;
    }

    protected void setPortFactory(AbstractCommPortFactory commPortFactory)
    {
        this.portFactory = commPortFactory;
    }

    protected AbstractCommPortFactory getSocketFactory()
    {
        return portFactory;
    }

    public SimpleServerPortFactory getServerPortFactory()
    {
        return serverPortFactory;
    }

    public void setServerSocketFactory(SimpleServerPortFactory serverPortFactory)
    {
        this.serverPortFactory = serverPortFactory;
    }

    protected CommPort getCommPort(String host, int baudrate, int databits, int stopbits, int parity, int delay) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException
    {
        return getServerPortFactory().createCommPort(host, baudrate, databits, stopbits, parity, delay, true, isReuseAddress());
//        return getServerPortFactory().createCommPort(uri.getHost(), getReceiveBacklog(), isReuseAddress());
    }

    private static int valueOrDefault(int value, int threshhold, int deflt)
    {
        if (value < threshhold)
        {
            return deflt;
        }
        else
        {
            return value;
        }
    }

    /**
     * Should the connection be checked before sending data?
     *
     * @return If true, the message adapter opens and closes the socket on intialisation.
     */
    public boolean isValidateConnections()
    {
        return validateConnections;
    }

    /**
     * @param validateConnections If true, the message adapter opens and closes the socket on intialisation.
     * @see #isValidateConnections()
     */
    public void setValidateConnections(boolean validateConnections)
    {
        this.validateConnections = validateConnections;
    }

    /**
     * @return true if the server socket sets SO_REUSEADDRESS before opening
     */
    public Boolean isReuseAddress()
    {
        return reuseAddress;
    }

    /**
     * This allows closed sockets to be reused while they are still in TIME_WAIT state
     *
     * @param reuseAddress Whether the server socket sets SO_REUSEADDRESS before opening
     */
    public void setReuseAddress(Boolean reuseAddress)
    {
        this.reuseAddress = reuseAddress;
    }

    public ExpiryMonitor getKeepAliveMonitor()
    {
        return keepAliveMonitor;
    }

    /**
     * @return keep alive timeout in Milliseconds
     */
    public int getKeepAliveTimeout()
    {
        return keepAliveTimeout;
    }

    /**
     * Sets the keep alive timeout (in Milliseconds)
     */
    public void setKeepAliveTimeout(int keepAliveTimeout)
    {
        this.keepAliveTimeout = keepAliveTimeout;

    }

}
