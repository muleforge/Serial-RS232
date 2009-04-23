/*
 * Generated by the Mule project wizard. http://mule.mulesource.org
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.comm;

import org.mule.api.registry.ServiceFinder;
import org.mule.api.registry.ServiceDescriptor;
import org.mule.api.registry.ServiceException;
import org.mule.util.PropertiesUtils;
import org.mule.util.ClassUtils;
import org.mule.transport.soap.i18n.SoapMessages;

import java.util.Properties;
import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

/**
 * org.mule.transport.comm
 *
 * @author Christopher Cheng
 * @version $Id: Mar 5, 2009 4:02:48 PM
 */
public class CommServiceFinder implements ServiceFinder
{
    /**
     * @deprecated We can use a more intelligent strategy for locating the service using the OSGi registry.
     */
    // //@Override
    public String findService(String service, ServiceDescriptor descriptor, Properties props) throws ServiceException
    {
//        Map finders = new TreeMap();
//        PropertiesUtils.getPropertiesWithPrefix(props, "finder.class", finders);
//
//        StringBuffer buf = new StringBuffer();
//        for (Iterator iterator = finders.entrySet().iterator(); iterator.hasNext();)
//        {
//            Map.Entry entry = (Map.Entry)iterator.next();
//            try
//            {
//                ClassUtils.loadClass(entry.getValue().toString(), getClass());
//                return getProtocolFromKey(entry.getKey().toString());
//            }
//            catch (ClassNotFoundException e1)
//            {
//                buf.append(entry.getValue().toString()).append("(").append(entry.getKey().toString()).append(
//                    ")").append(", ");
//            }
//        }
//        throw new ServiceException(SoapMessages.couldNotFindSoapProvider(buf.toString()));
        return null;
    }

    protected String getProtocolFromKey(String key)
    {
        return key.substring(key.lastIndexOf('.') + 1);
    }
}