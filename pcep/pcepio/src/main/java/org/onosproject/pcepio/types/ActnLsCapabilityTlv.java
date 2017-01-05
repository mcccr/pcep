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
 */
package org.onosproject.pcepio.types;

import java.util.Objects;

import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

/**
 * Provides TED Capability Tlv for ACTN. It is copied from class LsCapabilityTlv, but
 * variable TYPE changed from 65280 to 50003.
 */
public class ActnLsCapabilityTlv implements PcepValueType {

    /*
     * Reference :draft-dhodylee-pce-pcep-ls-03, section 9.1.1.
     *  0                   1                   2                   3
        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |               Type=[TBD5]     |            Length=4           |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                             Flags                           |R|
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

        这个TLV的类型是[TBD5]，有固定的4个字节的长度。 其值是一个简单的域，没有更细的分类（32 bits）：

        R（remote - 1 bit）：如果被PCC设置为1，则标识PCC能够上报通过其它方法（比如IGP，BGP-LS）学习到的远端的链路状态信息；
        如果被PCE设置为1，则标识PCE有能力接收远端链路状态信息。必须通信双方都设置该值为1，PCC才能有效上报远端链路状态信息。
        当通信双方都设置R Flag为1时，与IGP/BGP-LS identifier相关的TLVs都MUST 被编码。
        未分配的31 bits是预留位。它们MUST被设置为0，并且被接收的时候MUST被忽略。
     */

    protected static final Logger log = LoggerFactory.getLogger(ActnLsCapabilityTlv.class);

    public static final short TYPE = (short) 10003;
    public static final short LENGTH = 4;
    public static final int SET = 1;
    public static final byte RFLAG_CHECK = 0x01;

    private final boolean rFlag;
    private final int rawValue;
    private final boolean isRawValueSet;

    /**
     * Constructor to initialize raw Value.
     *
     * @param rawValue Flags
     */
    public ActnLsCapabilityTlv(final int rawValue) {
        this.rawValue = rawValue;
        this.isRawValueSet = true;
        int temp = rawValue;
        temp = temp & RFLAG_CHECK;
        if (temp == SET) {
            this.rFlag = true;
        } else {
            this.rFlag = false;
        }

    }

    /**
     * Constructor to initialize rFlag.
     *
     * @param rFlag R-flag
     */
    public ActnLsCapabilityTlv(boolean rFlag) {
        this.rFlag = rFlag;
        this.rawValue = 0;
        this.isRawValueSet = false;
    }

    /**
     * Returns R-flag.
     *
     * @return rFlag
     */
    public boolean getrFlag() {
        return rFlag;
    }

    /**
     * Returns an object of ActnLsCapabilityTlv.
     *
     * @param raw value Flags
     * @return object of ActnLsCapabilityTlv
     */
    public static ActnLsCapabilityTlv of(final int raw) {
        return new ActnLsCapabilityTlv(raw);
    }

    @Override
    public PcepVersion getVersion() {
        return PcepVersion.PCEP_1;
    }

    public int getInt() {
        return rawValue;
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
    public int hashCode() {
        if (isRawValueSet) {
            return Objects.hash(rawValue);
        } else {
            return Objects.hash(rFlag);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ActnLsCapabilityTlv) {
            ActnLsCapabilityTlv other = (ActnLsCapabilityTlv) obj;
            if (isRawValueSet) {
                return Objects.equals(this.rawValue, other.rawValue);
            } else {
                return Objects.equals(this.rFlag, other.rFlag);
            }
        }
        return false;
    }

    @Override
    public int write(ChannelBuffer c) {
        int iStartIndex = c.writerIndex();
        int temp = 0;
        c.writeShort(TYPE);
        c.writeShort(LENGTH);
        if (isRawValueSet) {
            c.writeInt(rawValue);
        } else {
            if (rFlag) {
                temp = temp | RFLAG_CHECK;
            }
            c.writeInt(temp);
        }
        return c.writerIndex() - iStartIndex;
    }

    /**
     * Reads channel buffer and returns object of ActnLsCapabilityTlv.
     *
     * @param c input channel buffer
     * @return object of ActnLsCapabilityTlv
     */
    public static ActnLsCapabilityTlv read(ChannelBuffer c) {
        return ActnLsCapabilityTlv.of(c.readInt());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("Type", TYPE)
                .add("Length", LENGTH)
                .add("Value", rawValue)
                .add("rFlag", rFlag)
                .toString();
    }
}
