Since I am in the middle of a project, I do not have much time for documentation. 
If you want to join as a developer to write the documentation, I am more than happy.
If you want to try this out, use this in your mule config. (comm://1 = COM1 or ttyS1)
Please note that you have to define one connector per comm port.

<custom-connector name="commConnector" class="org.mule.transport.comm.CommConnector">
</custom-connector>

<service name="YourOwnUMO">
  <inbound>
    <inbound-endpoint address="comm://1" connector-ref="commConnector">
      <property key="baudrate" value="9600"/>
      <property key="databits" value="8"/>
      <property key="stopbits" value="1"/>
      <property key="parity" value="0"/>
    </inbound-endpoint>
  </inbound>
  <component>
    <singleton-object class="com.skywidetech.YourObject">                    
    </singleton-object>                
  </component>
  <outbound>
    <filtering-router>
      <outbound-endpoint address="comm://1" connector-ref="commConnector" remoteSync="false" synchronous="true"/>
      <payload-type-filter expectedType="java.lang.String"/>
    </filtering-router>
    <custom-catch-all-strategy class="com.skywidetech.TestCatchAllStrategy"/>
  </outbound>
</service>
