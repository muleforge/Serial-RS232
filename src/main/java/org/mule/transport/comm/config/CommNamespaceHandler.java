/*
 * Generated by the Mule project wizard. http://mule.mulesource.org
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.comm.config;

import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.transport.tcp.TcpConnector;
import org.mule.transport.tcp.TcpProtocol;
import org.mule.transport.tcp.config.*;
import org.mule.transport.tcp.config.ByteOrMessageProtocolDefinitionParser;
import org.mule.transport.tcp.protocols.*;
import org.mule.transport.comm.CommConnector;
import org.mule.endpoint.URIBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Christopher Sze-wai Cheng (christopher@skywidetech.com)
 * Date: Mar 1, 2009
 * Time: 4:02:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommNamespaceHandler extends AbstractMuleNamespaceHandler
{

    public void init()
    {
        registerStandardTransportEndpoints(CommConnector.COMM, URIBuilder.SOCKET_ATTRIBUTES);
        registerConnectorDefinitionParser(CommConnector.class);
        registerBeanDefinitionParser("custom-protocol", new ChildDefinitionParser("commProtocol", null, TcpProtocol.class, true));
        registerBeanDefinitionParser("xml-protocol", new ChildDefinitionParser("commProtocol", XmlMessageProtocol.class));
        registerBeanDefinitionParser("xml-eof-protocol", new ChildDefinitionParser("commProtocol", XmlMessageEOFProtocol.class));
        registerBeanDefinitionParser("safe-protocol", new org.mule.transport.tcp.config.ByteOrMessageProtocolDefinitionParser(SafeProtocol.class, MuleMessageSafeProtocol.class));
        registerBeanDefinitionParser("length-protocol", new ByteOrMessageProtocolDefinitionParser(LengthProtocol.class, MuleMessageLengthProtocol.class));
        registerBeanDefinitionParser("eof-protocol", new ByteOrMessageProtocolDefinitionParser(EOFProtocol.class, MuleMessageEOFProtocol.class));
        registerBeanDefinitionParser("direct-protocol", new ByteOrMessageProtocolDefinitionParser(DirectProtocol.class, MuleMessageDirectProtocol.class));
        registerBeanDefinitionParser("streaming-protocol", new ByteOrMessageProtocolDefinitionParser(StreamingProtocol.class, MuleMessageDirectProtocol.class));
    }

}