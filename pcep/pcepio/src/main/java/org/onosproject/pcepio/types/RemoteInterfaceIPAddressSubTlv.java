/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.onosproject.pcepio.types;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.pcepio.exceptions.PcepParseException;
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides RemoteInterfaceIPAddressSubTlv.
 */
public class RemoteInterfaceIPAddressSubTlv implements PcepValueType {
    /* Reference  :[RFC3630]/2.5
     * 0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |              Type=4           |             Length=4N         |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                                                               |
     //                    Remote IP addresses (variable)           //
     |                                                               |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     */
    protected static final Logger log = LoggerFactory.getLogger(RemoteInterfaceIPAddressSubTlv.class);
    public static final short TYPE = 4;
    private short hLength;
    private final int[] rawValue;

    /**
     * Constructor to initialize rawValue.
     *
     * @param rawValue local IP address
     * @param hLength length
     */
    public RemoteInterfaceIPAddressSubTlv(int[] rawValue, short hLength) {
        this.rawValue = rawValue;
        if (0 == hLength) {
            this.hLength = (short) rawValue.length;
        } else {
            this.hLength = hLength;
        }
    }
    /**
     * Returns newly created RemoteInterfaceIPAddressSubTlv object.
     *
     * @param raw local IP address to a link.
     * @param hLength length
     * @return object of RemoteInterfaceIPAddressSubTlv

     */
    public static RemoteInterfaceIPAddressSubTlv of(final int[] raw, short hLength) {
        return new RemoteInterfaceIPAddressSubTlv(raw, hLength);
    }

    /**
     * Returns value of local IP address to a link.
     *
     * @return raw value
     */
    public int[] getValue() {
        return rawValue;
    }
    @Override
    public PcepVersion getVersion() {
        return PcepVersion.PCEP_1;
    }

    @Override
    public short getType() {
        return TYPE;
    }

    @Override
    public short getLength() {
        return hLength;
    }

    @Override
    public int write(ChannelBuffer c) {
        int iLenStartIndex = c.writerIndex();
        c.writeShort(TYPE);
        c.writeShort(hLength);
        for (int ip : rawValue) {
            c.writeInt(ip);
        }
        return c.writerIndex() - iLenStartIndex;
    }

    /**
     * Reads the channel buffer and returns object of RemoteInterfaceIPAddressSubTlv.
     *
     * @param c input channel buffer
     * @param hLength length
     * @return object of RemoteInterfaceIPAddressSubTlv
     * @throws PcepParseException hLength doesn't match.
     */
    public static PcepValueType read(ChannelBuffer c, short hLength) throws PcepParseException {
        if (hLength % 4 == 0) {
            int[] localIPAddresses = new int[hLength / 4];
            for (int i = 0; i < hLength / 4; i++) {
                localIPAddresses[i] = c.readInt();
            }
            return of(localIPAddresses, hLength);
        } else {
            throw new PcepParseException("The length of Local Interface IP Address Sub TLV is not multiple of 4");
        }
    }

}
