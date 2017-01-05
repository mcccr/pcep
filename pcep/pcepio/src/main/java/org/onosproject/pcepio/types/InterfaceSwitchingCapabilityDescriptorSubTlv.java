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

import com.google.common.base.MoreObjects;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Provides Interface Switching Capability Descriptor Sub TLV, only Lambda-Switch Capable enabled.
 */
public class InterfaceSwitchingCapabilityDescriptorSubTlv implements PcepValueType {
    /*
     * Reference :[RFC4203]/1.4
     *  0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |              Type=15          |             Length=36         |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |SwitchingCap150|   Encoding=8  |           Reserved            |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 0              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 1              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 2              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 3              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 4              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 5              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 6              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                  Max LSP Bandwidth at priority 7              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+


     The Switching Capability (Switching Cap, RFC4203/1.4) field contains one of the following values:
          1     Packet-Switch Capable-1 (PSC-1)
          2     Packet-Switch Capable-2 (PSC-2)
          3     Packet-Switch Capable-3 (PSC-3)
          4     Packet-Switch Capable-4 (PSC-4)
          51    Layer-2 Switch Capable  (L2SC)
          100   Time-Division-Multiplex Capable (TDM)
          150   Lambda-Switch Capable   (LSC)
          200   Fiber-Switch Capable    (FSC)
     In this class, it MUST be 150.

     The LSP Encoding Type (Encoing, RFC3471/3.1.1) field contains one of the following values:
          1         Packet
          2         Ethernet
          3         ANSI/ETSI PDH
          4         Reserved
          5         SDH ITU-T G.707 / SONET ANSI T1.105
          6         Reserved
          7         Digital Wrapper
          8         Lambda (photonic)
          9         Fiber
         10         Reserved
         11         FiberChannel
     In this class, it MUST be 8.
     */
    protected static final Logger log = LoggerFactory.getLogger(InterfaceSwitchingCapabilityDescriptorSubTlv.class);

    public static final short TYPE = (short) 15;
    public static final short LENGTH = 36;
    public static final byte SWITCHINGCAP = (byte) 0x96;
    public static final byte ENCODING = 0x08;
    public static final short RESERVED = 0;

    private final int[] priorities;

    public int[] getPriorities() {
        return priorities;
    }

    /**
     *
     * @param priorities 8 priorities of Max LSP Bandwidth
     */
    public InterfaceSwitchingCapabilityDescriptorSubTlv(int[] priorities) {
        this.priorities = priorities;
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
        return LENGTH;
    }

    @Override
    public int write(ChannelBuffer c) {
        int iLenStartIndex = c.writerIndex();
        c.writeShort(TYPE);
        c.writeShort(LENGTH);
        c.writeByte(SWITCHINGCAP);
        c.writeByte(ENCODING);
        c.writeShort(RESERVED);
        for (int i : priorities) {
            c.writeInt(i);
        }
        return c.writerIndex() - iLenStartIndex;
    }


    /**
     * Reads the channel buffer and returns object of InterfaceSwitchingCapabilityDescriptorSubTlv.
     *
     * @param c input channel buffer
     * @return object of InterfaceSwitchingCapabilityDescriptorSubTlv
     */
    public static PcepValueType read(ChannelBuffer c) {
        // skip first int because it contains fixed value link SwitchingCap, Encoding and Reserved.
        int[] priorities = new int[8];
        c.readInt();
        for (int i = 0; i < 8; i++) {
            priorities[i] = c.readInt();
        }

        return new InterfaceSwitchingCapabilityDescriptorSubTlv(priorities);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof InterfaceSwitchingCapabilityDescriptorSubTlv) {
            InterfaceSwitchingCapabilityDescriptorSubTlv other = (InterfaceSwitchingCapabilityDescriptorSubTlv) obj;
            return Objects.equals(this.priorities, other.priorities);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(priorities);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("Type", TYPE)
                .add("Length", LENGTH)
                .add("SwitchingCap", SWITCHINGCAP)
                .add("Encoding", ENCODING)
                .add("priorities", priorities)
                .toString();
    }
}
