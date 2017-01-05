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

import com.google.common.base.Objects;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides Node IPv4 Local Address sub-TLV as part of the Node Attribute TLV.
 */
public class NodeIPv4LocalAddressSubTlv implements PcepValueType {
    /*
     * Reference: [RFC5786]/4.1
     * The Node IPv4 Local Address sub-TLV has a type of 1 and contains one
     * or more local IPv4 addresses.  It has the following format:
     *
     * 0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |              type=1           |             Length            |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     | Prefix Len 1  |          IPv4 Prefix 1                        |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |Prefix 1 cont. |                                               :
     +-+-+-+-+-+-+-+-+                                               ~
     :                               .                               :
     ~                               .               +-+-+-+-+-+-+-+-+
     :                               .               | Prefix Len n  |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                          IPv4 Prefix n                        |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

     Prefix Len: 1 byte, at most 32.
     IPv4 Prefix: 4 byte.
     *
     */
    protected static final Logger log = LoggerFactory.getLogger(NodeIPv4LocalAddressSubTlv.class);
    public static final short TYPE = 1;
    private final short hLength;
    private final Pair<Byte, Integer>[] ipPrefix;

    /**
     * Constructor to initialize NodeIPv4LocalAddressSubTlv value.
     * @param hLength length of Value
     * @param ipPrefix a list of tuple \<Prefix Len, IPv4 Prefix\>
     */
    public NodeIPv4LocalAddressSubTlv(short hLength, Pair<Byte, Integer>[] ipPrefix) {
        this.hLength = hLength;
        this.ipPrefix = ipPrefix;
    }

    /**
     * returns IP Prefix tuple.
     * @return IP Prefix tuple
     */
    public Pair<Byte, Integer>[] getIpPrefix() {
        return ipPrefix;
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
        for (Pair<Byte, Integer> prefix : ipPrefix) {
            c.writeByte(prefix.getKey());
            c.writeInt(prefix.getValue());
        }
        return c.writerIndex() - iLenStartIndex;
    }

    /**
     * Reads from channel buffer and returns object of PortLabelRestrictionsSubTlv.
     *
     * @param c input channel buffer
     * @param hLength length
     * @return object of PortLabelRestrictionsSubTlv
     */
    public static PcepValueType read(ChannelBuffer c, short hLength) {
        int num = hLength / 5;
        Pair<Byte, Integer>[] prefixes = new Pair[num];
        for (int i = 0; i < num; i++) {
            byte prefixLen = c.readByte();
            int prefix = c.readInt();
            prefixes[i] = Pair.of(prefixLen, prefix);
        }
        return new NodeIPv4LocalAddressSubTlv(hLength, prefixes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeIPv4LocalAddressSubTlv)) {
            return false;
        }
        NodeIPv4LocalAddressSubTlv that = (NodeIPv4LocalAddressSubTlv) o;
        return hLength == that.hLength &&
                Objects.equal(ipPrefix, that.ipPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(hLength, ipPrefix);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("hLength", hLength)
                .add("ipPrefix", ipPrefix)
                .toString();
    }
}
