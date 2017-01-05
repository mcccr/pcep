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
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Objects;

/**
 * Provide Link Type in Link TLV.
 */
public class LinkTypeSubTlv implements PcepValueType {
    /* Reference  :[RFC3630]/2.5
     * 0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |              Type=1           |             Length=1         |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |Link Type      |
     +-+-+-+-+-+-+-+-+

     Link Type:
         1 - Point-to-Point
         2 - Multi-access
     */
    protected static final Logger log = LoggerFactory.getLogger(LinkTypeSubTlv.class);
    public static final short TYPE = 1;
    public static final short LENGTH = 1;
    private final byte linkType;

    /**
     * Constructor to initialize linkType.
     * @param linkType link type
     */
    public LinkTypeSubTlv(byte linkType) {
        this.linkType = linkType;
    }

    /**
     * returns Link Type.
     * @return Link Type
     */
    public byte getLinkType() {
        return linkType;
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
        c.writeByte(linkType);
        return c.writerIndex() - iLenStartIndex;
    }

    /**
     * Reads the channel buffer and returns object of LinkTypeTlv.
     *
     * @param c input channel buffer
     * @return object of LinkTypeTlv
     */
    public static PcepValueType read(ChannelBuffer c) {
        byte linkTYpe = c.readByte();
        return new LinkTypeSubTlv(linkTYpe);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LinkTypeSubTlv) {
            LinkTypeSubTlv other = (LinkTypeSubTlv) obj;
            return Objects.equals(linkType, other.linkType);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("Type", TYPE)
                .add("Length", LENGTH)
                .add("linkType", linkType)
                .toString();
    }
}
