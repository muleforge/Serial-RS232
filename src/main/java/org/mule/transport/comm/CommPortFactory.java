/*
 * Generated by the Mule project wizard. http://mule.mulesource.org
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.comm;

import org.mule.transport.tcp.AbstractTcpSocketFactory;
import org.mule.transport.tcp.TcpSocketKey;

import javax.comm.*;
import java.net.Socket;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Christopher Cheng
 * Date: Mar 1, 2009
 * Time: 2:33:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommPortFactory extends AbstractCommPortFactory
{
    // todo make it in CommPortKey
    int delay = 100;

    protected CommPort createPort(CommPortKey key) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException
    {
        CommPortIdentifier com = CommPortIdentifier.getPortIdentifier(key.getPort());
        System.out.println("CommPortFactory delay=" + delay);
        SerialPort serialPort = (SerialPort) com.open(key.getPort(), delay);
        CommConnector.getPortsMap().put(key.getPort(), serialPort);
        System.out.println("put " + key.getPort() + " into the map"); 

        return serialPort;
        // todo we still need to set this
//                com1.setSerialPortParams(Integer.parseInt(Globals.PORT1_BAUDRATE), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    }

}
