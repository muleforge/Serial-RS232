package org.mule.transformer.simple;

import org.mule.transformer.AbstractTransformer;
import org.mule.api.transformer.TransformerException;

import java.io.DataInputStream;

/**
 * org.mule.transformer.simple
 *
 * @author Christopher Cheng
 * @version $Id: Apr 23, 2009 6:07:30 PM
 */
public class ByteArrayToModbusResponse extends AbstractTransformer
{
    public ByteArrayToModbusResponse()
    {
        registerSourceType(byte[].class);
        setReturnClass(ModbusResponse.class);
    }

    public Object doTransform(Object src, String outputEncoding) throws TransformerException
    {
//        System.out.println("ByteArrayToModbusResponse called");

        try {
            BytesInputStream m_ByteIn = new BytesInputStream((byte[]) src);
            DataInputStream m_Input = new DataInputStream(m_ByteIn);
            byte[] buffer = m_ByteIn.getBuffer();

            //read to byte length of message
            if (m_Input.read(buffer, 0, 6) == -1) {
                throw new ModbusIOException("Premature end of stream (Header truncated).");
            }
            //extract length of bytes following in message
            int bf = ModbusUtil.registerToShort(buffer, 4);
            //read rest
            if (m_Input.read(buffer, 6, bf) == -1) {
                throw new ModbusIOException("Premature end of stream (Message truncated).");
            }
            m_ByteIn.reset(buffer, (6 + bf));
            m_ByteIn.skip(7);
            int functionCode = m_ByteIn.readUnsignedByte();
            m_ByteIn.reset();
            ModbusResponse res = ModbusResponse.createModbusResponse(functionCode);
            res.readFrom(m_ByteIn);

            return res;
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }

    }
}