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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.onosproject.pcepio.protocol.PcepVersion;
import java.util.Objects;


/**
 * indicates label information in LabelSubObject in PcepEroObject in PCInitiate message.
 */
public class GeneralizedLabelObject {

    /* Reference RFC3473#section-2.3
     * Generalized Label Object
     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |            Length             | Class-Num (16)|   C-Type (2)  |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                             Label                             |
     |                              ...                              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

     Label : defined in RFC3471. We use Port and Wavelength Labels format described
             in https://tools.ietf.org/html/rfc3471#section-3.2. As follow:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                             Label                             |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

     *
     */


    protected static final Logger log = LoggerFactory.getLogger(GeneralizedLabelObject.class);
    private static final short LENGTH = 8;
    private static final byte classNum = 16;
    public static final byte cType = 2;
    private final int label;

    /**
     *
     * @param label
     */
    public GeneralizedLabelObject(int label) {
        this.label = label;
    }

    /**
     *
     * @param label
     * @return an new object of GeneralizedLabelObject
     */
    public static GeneralizedLabelObject of(int label) {
        return new GeneralizedLabelObject(label);
    }

    /**
     *
     * @return Class-Num.
     */
    public byte getClassNum() {
        return classNum;
    }

    /**
     *
     * @return C-Type
     */
    public byte getCType() {
        return cType;
    }

    public PcepVersion getVersion() {
        return PcepVersion.PCEP_1;
    }

    public short getLength() {
        return LENGTH;
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GeneralizedLabelObject) {
            GeneralizedLabelObject other = (GeneralizedLabelObject) obj;
            return Objects.equals(this.label, other.label);
        }
        return false;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("Length", LENGTH)
                .add("CLASS-NUM", classNum)
                .add("C-TYPE", cType)
                .add("Label", label)
                .toString();
    }

    public static GeneralizedLabelObject read(ChannelBuffer c) {
        c.readShort();
        c.readByte();
        c.readByte();
        int label = c.readInt();
        return new GeneralizedLabelObject(label);
    }

    public int write(ChannelBuffer c) {
        int iLenStartIndex = c.writerIndex();
        c.writeShort(LENGTH);
        c.writeByte(classNum);
        c.writeByte(cType);
        c.writeInt(label);

        return c.writerIndex() - iLenStartIndex;
    }
}















