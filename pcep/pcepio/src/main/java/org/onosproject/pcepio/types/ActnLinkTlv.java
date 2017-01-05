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
 * Provides Link TLV contained by LS Object typed 2.
 * For ACTN.
 */
public class ActnLinkTlv implements PcepValueType {
    /*
     * Defined by SDON group.

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |           Type=10001          |             Length            |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                                                               |
     //               ACTN Link  Sub-TLVs (variable)                //
     |                                                               |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

     The Link Sub-TLVs can be a list of LinkType/ LinkID/ LocalInterfaceIPAddress/
     RemoteInterfaceIPAddress/ TEMetric/ InterfaceSwitchingCapabilityDescriptor/
     SharedRiskLinkGroup/ PortLabelRestriction.
     */
    protected static final Logger log = LoggerFactory.getLogger(ActnLinkTlv.class);

    public static final short TYPE = (short) 10001;
    public short hLength;

    public static final int TLV_HEADER_LENGTH = 4;

    // LinkDescriptors Sub-TLVs (variable)
    private List<PcepValueType> actnLinkSubTLVs;

    public ActnLinkTlv(short hLength, List<PcepValueType> actnLinkSubTLVs) {
        this.hLength = hLength;
        this.actnLinkSubTLVs = actnLinkSubTLVs;
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

        ListIterator<PcepValueType> listIterator = actnLinkSubTLVs.listIterator();

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
     * Reads channel buffer and returns object of ActnLinkTlv.
     *
     * @param c input channel buffer
     * @param length length
     * @return object of LinkDescriptorsTlv
     * @throws PcepParseException if mandatory fields are missing
     */
    public static PcepValueType read(ChannelBuffer c, short length) throws PcepParseException {
        // ACTN Link Sub-TLVs (variable)
        List<PcepValueType> actnLinkSubTLVs = new LinkedList<>();

        ChannelBuffer tempCb = c.readBytes(length);

        while (TLV_HEADER_LENGTH <= tempCb.readableBytes()) {

            PcepValueType tlv;
            short hType = tempCb.readShort();
            short hLength = tempCb.readShort();
            log.debug("sub Tlv Length " + hLength);
            switch (hType) {
                case LinkTypeSubTlv.TYPE:
                    tlv = LinkTypeSubTlv.read(tempCb);
                    break;
                case LinkIdSubTlv.TYPE:
                    tlv = LinkIdSubTlv.read(tempCb);
                    break;
                case LocalInterfaceIPAddressSubTlv.TYPE:
                    tlv = LocalInterfaceIPAddressSubTlv.read(tempCb, hLength);
                    break;
                case RemoteInterfaceIPAddressSubTlv.TYPE:
                    tlv = RemoteInterfaceIPAddressSubTlv.read(tempCb, hLength);
                    break;
                case TEMetricSubTlv.TYPE:
                    tlv = TEMetricSubTlv.read(tempCb);
                    break;
                case InterfaceSwitchingCapabilityDescriptorSubTlv.TYPE:
                    tlv = InterfaceSwitchingCapabilityDescriptorSubTlv.read(tempCb);
                    break;
                case SrlgSubTlv.TYPE:
                    tlv = SrlgSubTlv.read(tempCb, hLength);
                    break;
                case PortLabelRestrictionsSubTlv.TYPE:
                    tlv = PortLabelRestrictionsSubTlv.read(tempCb, hLength);
                    break;
                case  ActnAvailableLabelsFieldSubTlv.TYPE:
                    tlv = ActnAvailableLabelsFieldSubTlv.read(tempCb, hLength);
                    break;
                default:
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
            actnLinkSubTLVs.add(tlv);
        }

        if (0 < tempCb.readableBytes()) {

            throw new PcepParseException("Sub Tlv parsing error. Extra bytes received.");
        }
        return new ActnLinkTlv((short) 1, actnLinkSubTLVs);
    }

    public List<PcepValueType> getActnLinkSubTLVs() {
        return actnLinkSubTLVs;
    }

    public void setActnLinkSubTLVs(List<PcepValueType> actnLinkSubTLVs) {
        this.actnLinkSubTLVs = actnLinkSubTLVs;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("Type", TYPE)
                .add("Length", hLength)
                .add("actnLinkSubTLVs", actnLinkSubTLVs)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(actnLinkSubTLVs.hashCode());
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
        if (obj instanceof ActnLinkTlv) {
            int countObjSubTlv = 0;
            int countOtherSubTlv = 0;
            boolean isCommonSubTlv = true;
            ActnLinkTlv other = (ActnLinkTlv) obj;
            Iterator<PcepValueType> objListIterator = ((ActnLinkTlv) obj).actnLinkSubTLVs.iterator();
            countObjSubTlv = ((ActnLinkTlv) obj).actnLinkSubTLVs.size();
            countOtherSubTlv = other.actnLinkSubTLVs.size();
            if (countObjSubTlv != countOtherSubTlv) {
                return false;
            } else {
                while (objListIterator.hasNext() && isCommonSubTlv) {
                    PcepValueType subTlv = objListIterator.next();
                    isCommonSubTlv = Objects.equals(actnLinkSubTLVs.contains(subTlv),
                            other.actnLinkSubTLVs.contains(subTlv));
                }
                return isCommonSubTlv;
            }
        }
        return false;
    }
}
