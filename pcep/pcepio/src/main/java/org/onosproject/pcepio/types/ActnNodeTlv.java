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
import org.onosproject.pcepio.exceptions.PcepParseException;
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * Provides Node TLV contained by LS Object typed 2.
 * For ACTN.
 */
public class ActnNodeTlv implements PcepValueType {
    /*
     * Defined by SDON group.

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |           Type=10002          |             Length            |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                                                               |
     //               ACTN Node  Sub-TLVs (variable)                //
     |                                                               |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

     The Node Sub-TLVs can be a list of Node IPv4 Local Address Sub TLV.
     */
    protected static final Logger log = LoggerFactory.getLogger(ActnNodeTlv.class);

    public static final short TYPE = (short) 10002;
    public short hLength;

    public static final int TLV_HEADER_LENGTH = 4;

    // LinkDescriptors Sub-TLVs (variable)
    private List<PcepValueType> actnNodeSubTLVs;

    public ActnNodeTlv(short hLength, List<PcepValueType> actnNodeSubTLVs) {
        this.hLength = hLength;
        this.actnNodeSubTLVs = actnNodeSubTLVs;
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
        int tlvStartIndex = c.writerIndex();
        c.writeShort(TYPE);
        int tlvLenIndex = c.writerIndex();
        hLength = 0;
        c.writeShort(hLength);

        ListIterator<PcepValueType> listIterator = actnNodeSubTLVs.listIterator();

        while (listIterator.hasNext()) {
            PcepValueType tlv = listIterator.next();

            tlv.write(c);

            // need to take care of padding
            int pad = tlv.getLength() % 4;

            if (0 != pad) {
                pad = 4 - pad;
                for (int i = 0; i < pad; ++i) {
                    c.writeByte((byte) 0);
                }
            }
        }

        hLength = (short) (c.writerIndex() - tlvStartIndex);
        c.setShort(tlvLenIndex, (hLength - TLV_HEADER_LENGTH));

        return c.writerIndex() - tlvStartIndex;
    }


    /**
     * Reads channel buffer and returns object of ActnNodeTlv.
     *
     * @param c input channel buffer
     * @param length length
     * @return object of LinkDescriptorsTlv
     * @throws PcepParseException if mandatory fields are missing
     */
    public static PcepValueType read(ChannelBuffer c, short length) throws PcepParseException {
        // ACTN Node Sub-TLVs (variable)
        List<PcepValueType> actnNodeSubTLVs = new LinkedList<>();

        ChannelBuffer tempCb = c.readBytes(length);

        while (TLV_HEADER_LENGTH <= tempCb.readableBytes()) {

            PcepValueType tlv;
            short hType = tempCb.readShort();
            short hLength = tempCb.readShort();
            log.debug("sub Tlv Length" + hLength);
            if (hType == NodeIPv4LocalAddressSubTlv.TYPE) {
                tlv = NodeIPv4LocalAddressSubTlv.read(tempCb, hLength);
            } else {
                throw new PcepParseException("Unsupported Sub TLV type:" + hType);
            }

            // Check for the padding
            int pad = hLength % 4;
            if (0 < pad) {
                pad = 4 - pad;
                if (pad <= tempCb.readableBytes()) {
                    tempCb.skipBytes(pad);
                }
            }
            actnNodeSubTLVs.add(tlv);
        }

        if (0 < tempCb.readableBytes()) {

            throw new PcepParseException("Sub Tlv parsing error. Extra bytes received.");
        }
        return new ActnNodeTlv((short) 0, actnNodeSubTLVs);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("Type", TYPE)
                .add("Length", hLength)
                .add("actnNodeSubTLVs", actnNodeSubTLVs)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(actnNodeSubTLVs.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        /*
         * Here we have a list of Tlv so to compare each sub tlv between the object
         * we have to take a list iterator so one by one we can get each sub tlv object
         * and can compare them.
         * it may be possible that the size of 2 lists is not equal so we have to first check
         * the size, if both are same then we should check for the subtlv objects otherwise
         * we should return false.
         */
        if (obj instanceof ActnNodeTlv) {
            int countObjSubTlv = 0;
            int countOtherSubTlv = 0;
            boolean isCommonSubTlv = true;
            ActnNodeTlv other = (ActnNodeTlv) obj;
            Iterator<PcepValueType> objListIterator = ((ActnNodeTlv) obj).actnNodeSubTLVs.iterator();
            countObjSubTlv = ((ActnNodeTlv) obj).actnNodeSubTLVs.size();
            countOtherSubTlv = other.actnNodeSubTLVs.size();
            if (countObjSubTlv != countOtherSubTlv) {
                return false;
            } else {
                while (objListIterator.hasNext() && isCommonSubTlv) {
                    PcepValueType subTlv = objListIterator.next();
                    isCommonSubTlv = Objects.equals(actnNodeSubTLVs.contains(subTlv),
                            other.actnNodeSubTLVs.contains(subTlv));
                }
                return isCommonSubTlv;
            }
        }
        return false;
    }

    public List<PcepValueType> getActnNodeSubTLVs() {
        return actnNodeSubTLVs;
    }

    public void setActnNodeSubTLVs(List<PcepValueType> actnNodeSubTLVs) {
        this.actnNodeSubTLVs = actnNodeSubTLVs;
    }
}
