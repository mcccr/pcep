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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;


/**
 * Provide Available Labels Field Sub TLV in Link TLV [RFC7579].
 */
public class ActnAvailableLabelsFieldSubTlv implements PcepValueType {
 /*
  * Sub-Tlv's TYPE set to 10004, see
  * https://tower.im/projects/ac9283eadfb44344b96da5273582ca93/docs/729449f2c41a4c43a3c14e1d4244a9ee/
  *
  * Reference RFC7579/ 2.4. Available Labels Field


   0                   1                   2                   3
   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  |     PRI       |              Reserved                         |
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
  |                     Label Set Field                           |
  :                                                               :
  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

  PRI: 0xFF. Indicate that it can be applied to all priorities.
  Reserved: set 0.
  Label Set Field: See RFC7579/ 2.4
  */


    protected static final Logger log = LoggerFactory.getLogger(ActnAvailableLabelsFieldSubTlv.class);
    public static final short TYPE = 10004;

    protected static final byte reserved1 = 0;
    protected static final byte reserved2 = 0;
    public final byte pri;

    private final short hLength;
    private final InternalLabelRange labelSetField;

    public InternalLabelRange getLabelSetField() {
        return labelSetField;
    }

    public ActnAvailableLabelsFieldSubTlv(InternalLabelRange labelSetField, short hLength, byte pri) {
        if (0 == hLength) {
            this.hLength = (short) (labelSetField.length() +4);
        } else {
            this.hLength = hLength;
        }
        this.labelSetField = labelSetField;
        this.pri = pri;
    }

    /**
     * returns PRI.
     * @return PRI
     */
    public byte getPri() {
        return pri;
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
        c.writeByte(pri);
        c.writeByte(reserved1);
        c.writeShort(reserved2);
        labelSetField.write(c);
        return c.writerIndex() - iLenStartIndex;
    }

    public static PcepValueType read(ChannelBuffer c, short hLength) {
        byte pri = c.readByte();
        c.readByte();
        c.readShort();
        try {
            InternalLabelRange labelSetFied = InternalLabelRange.read(c, hLength - 4);
            return new ActnAvailableLabelsFieldSubTlv(labelSetFied, hLength, pri);
        } catch (PcepParseException e) {
            log.error(e.getMessage());
            return null;
        }

    }

    public List<Boolean> getLabels() {
       return labelSetField.getLabels();

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActnAvailableLabelsFieldSubTlv)) {
            return false;
        }
        ActnAvailableLabelsFieldSubTlv that = (ActnAvailableLabelsFieldSubTlv) o;
        return hLength == that.hLength &&
                pri == that.pri && Objects.equal(labelSetField, that.labelSetField);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(labelSetField, hLength, pri);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("labelSetFields", labelSetField)
                .add("hLength", hLength)
                .add("matrixId", pri)
                .toString();
    }
}





