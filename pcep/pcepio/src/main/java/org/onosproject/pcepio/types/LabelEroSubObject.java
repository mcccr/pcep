package org.onosproject.pcepio.types;

import com.google.common.base.MoreObjects;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.pcepio.protocol.PcepVersion;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.Objects;

/**
 * Created by mcccr on 16-6-15.
 */



public class LabelEroSubObject implements PcepValueType{
    /*
     *
      0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |L|    Type     |     Length    |U|   Reserved  |   C-Type      |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     |                             Label Object                      |
     |                              ...                              |
     +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

     L —— 根据RFC3471，必须设置为0。
     U —— 根据RFC3471，表示LSP的方向，如果设置为1,则表示上游方向的LSP;如果设置为0,则表示下游方向的LSP。这个标志位仅仅对于双向LSP有效。
     Label（64-bit） —— 根据RFC3471，表示Label信息。这里的Label表示的是光的波长，不是20-bit的MPLS的Label。其解释取决于链路类型。
     Type —— 3
     Length —— 表示整个subobject的长度，以byte为单位。
     C-Type —— 是所包含的Label Object的C-Type，两者值一样。关于包含的Label Object，我表示暂时没时间看，不过就在这个RFC3473中。可以自己定义。
     *
     *
     */
    protected static final Logger log = LoggerFactory.getLogger(LabelEroSubObject.class);

    public static final byte TYPE = 0x03;
    public static final byte LENGTH = 12;
    public static final byte LBIT = 0;
    public static final int SHIFT_LBIT_POSITION = 7;
    public static final byte RESERVED = 0;
    private final byte CTYPE;
    private final GeneralizedLabelObject content;

    /**
     *
     * @param cType c-type
     * @param content content
     */
    public LabelEroSubObject(byte cType, GeneralizedLabelObject content) {
        this.CTYPE = cType;
        this.content = content;
    }

    /**
     *
     * @param cType c-type
     * @param content content
     * @return LabelEroSubObject
     */
    public static LabelEroSubObject of(byte cType, GeneralizedLabelObject content) {
        return new LabelEroSubObject(cType, content);
    }

    /**
     *
     * @return c-type
     */
    public byte getCTYPE() {
        return CTYPE;
    }

    /**
     *
     * @return content
     */
    public GeneralizedLabelObject getContent() {
        return content;
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
    public int hashCode() {
        return Objects.hash(CTYPE, content);
    }

    public boolean equals(java.lang.Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LabelEroSubObject) {
            LabelEroSubObject other = (LabelEroSubObject) obj;
            return Objects.equals(this.CTYPE, other.CTYPE) && Objects.equals(this.content, other.content);
        }
        return false;
    }

    @Override
    public int write(ChannelBuffer c) {
        int iStartIndex = c.writerIndex();
        byte bValue = LBIT;
        bValue = (byte) (bValue << SHIFT_LBIT_POSITION);;
        bValue = (byte) (bValue | TYPE);
        c.writeShort(bValue);
        c.writeShort(LENGTH);
        c.writeByte(RESERVED);
        c.writeByte(CTYPE);
        content.write(c);
        return c.writerIndex() - iStartIndex;
    }

    /**
     * Reads the channel buffer and returns object of LabelSubObject.
     *
     * @param c type of channel buffer
     * @return object of LabelSubObject
     */
    public static PcepValueType read(ChannelBuffer c) {
        c.readByte();
        byte cType = c.readByte();
        GeneralizedLabelObject content = GeneralizedLabelObject.read(c);
        return new LabelEroSubObject(cType, content);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("type", TYPE)
                .add("Length", LENGTH)
                .add("C-type", CTYPE)
                .add("content", content)
                .toString();
    }
}

















