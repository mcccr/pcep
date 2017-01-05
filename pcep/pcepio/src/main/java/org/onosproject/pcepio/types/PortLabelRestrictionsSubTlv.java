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
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.pcepio.exceptions.PcepParseException;
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Provide Port Label Restrictions Sub TLV in Link TLV [RFC3630].
 */
public class PortLabelRestrictionsSubTlv implements PcepValueType {
    /*
     * Reference "[RFC7580] /3.1 Port Label Restrictions
     * type is 34.
     *
     * Reference :[RFC7579] /2.2
     *
     * 0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |   MatrixID    |    RstType    | Switching Cap |     Encoding  |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |     Additional Restriction Parameters per Restriction Type    |
     :                                                               :
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

     MatrixID: eithetemplater is the value in the corresponding Connectivity
                Matrix Field or takes the value 0xFF to indicate the restriction
                applies to the port regardless of any connectivity matrix.
     RstType (Restriction Type) can take the following values and meanings:
                2: LABEL_RANGE (Label range device with a movable center label and
                width).  See Section 2.2.3 for details.
     Switching Cap: LSC 150
     Encoding: 8
     The Additional Restriction Parameters per RestrictionType field is an
                optional field that describes additional restriction parameters for
                each RestrictionType pertaining to specific protocols.
     */
    protected static final Logger log = LoggerFactory.getLogger(PortLabelRestrictionsSubTlv.class);

    public static final short TYPE = 34;
    public static final byte RSTTYPE = 2;
    public static final byte SWITCHINGCAP = (byte) 150;
    public static final byte ENCODING = 8;
    private final InternalLabelRangeRP restrictionParams;
    private final short hLength;
    // Whether matrixId should be 0xFF, TBD.
    private final byte matrixId;

    /**
     * Constructor to initialize PortLabelRestrictionsSubTlv value.
     * @param restrictionParams Restriction Parameters.
     * @param hLength length of Value in bytes.
     * @param matrixId Matrix ID.
     */
    public PortLabelRestrictionsSubTlv(InternalLabelRangeRP restrictionParams, short hLength, byte matrixId) {
        this.restrictionParams = restrictionParams;
        if (0 == hLength) {
            this.hLength = (short) (restrictionParams.length() + 4);
        } else {
            this.hLength = hLength;
        }
        this.matrixId = matrixId;
    }

    /**
     * returns Rst Type.
     * @return Rst Type
     */
    public static byte getRsttype() {
        return RSTTYPE;
    }

    /**
     * returns Switching Capability.
     * @return Switching Capability
     */
    public static byte getSwitchingCap() {
        return SWITCHINGCAP;
    }

    /**
     * returns Encoding.
     * @return Encoding
     */
    public static byte getEncoding() {
        return ENCODING;
    }

    /**
     * returns Restriction Parameters.
     * @return Restriction Parameters
     */
    public InternalLabelRangeRP getRestrictionParams() {
        return restrictionParams;
    }



    /**
     * returns Matrix ID.
     * @return Matrix ID
     */
    public byte getMatrixId() {
        return matrixId;
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
        c.writeByte(matrixId);
        c.writeByte(RSTTYPE);
        c.writeByte(SWITCHINGCAP);
        c.writeByte(ENCODING);
        restrictionParams.write(c);
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
        byte matrixId = c.readByte();
        c.readByte();
        c.readShort();
        try {
            InternalLabelRangeRP labelRangeRP = InternalLabelRangeRP.read(c, hLength - 4);
            return new PortLabelRestrictionsSubTlv(labelRangeRP, hLength, matrixId);
        } catch (PcepParseException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<Boolean> getLabels() {
        return restrictionParams.getLabelList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PortLabelRestrictionsSubTlv)) {
            return false;
        }
        PortLabelRestrictionsSubTlv that = (PortLabelRestrictionsSubTlv) o;
        return hLength == that.hLength &&
                matrixId == that.matrixId &&
                Objects.equal(restrictionParams, that.restrictionParams);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(restrictionParams, hLength, matrixId);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("restrictionParams", restrictionParams)
                .add("hLength", hLength)
                .add("matrixId", matrixId)
                .toString();
    }
}

/**
 * Provides Restriction Parameters in Restriction Type equals 2, e.g. Label Range.
 */
