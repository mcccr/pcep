package org.onosproject.pcepio.types;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.jboss.netty.buffer.ChannelBuffer;
import org.onosproject.pcepio.exceptions.PcepParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenrong on 2016-05-27.
 */
public class InternalLabelRange {

    /*      0                   1                   2                   3
             0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            |  4    | Num Labels = 40       |    Length = 16 bytes          |
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            |Grid |  C.S. |      Reserved   | n  for lowest frequency = -11 |
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            |1 0 0 0 0 1 0 0 0 0 0 1 0 0 0 0 0 0 0 1 1 0 0 0 0 0 0 0 0 0 0 0|
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
            |1 0 0 0 0 0 1 0|   Not used in 40 Channel system (all zeros)   |
            +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    */

    public static final byte ACTION = 64;
    public static final byte NUMLABELS = 40;
    public static final short LENGTH = 16;
    private static final byte GRID_CS = 17;
    private static final byte RESERVED = 0;
    private final short LOWEST_FREQUENCY = 0;
    private List<Boolean> labelList;
    public static final byte PADDING1 = 0;
    public static final short PADDING2 = 0;
    //private boolean[] lables;


    /**
     *
     * @param labelList
     */
    public InternalLabelRange(List labelList) {
        this.labelList = labelList;

    }

    public List getBooleanArray(boolean[] a) {
        List booleanArrayList =  Lists.newArrayList();
        int num = a.length / 8;
        int arrayNum = a.length / num;
        for(int j = 0; j < arrayNum; j ++) {
            for(int i = 0; i < 8; i ++) {
                boolean[] s = new boolean[8];
                s[i] = a[i +(j * 8)];
                booleanArrayList.add(s);
            }
        }
        return booleanArrayList;
    }



    public static boolean[] byte2boolean(byte a) {
        boolean[] array = new boolean[8];
        for(int i = 7; i >= 0;i --) {
            array[i] = (a & 1) == 1;
            a = (byte) (a >> 1);
        }
        return array;
    }
    // 1 byte translates to a boolean[8];

    public static byte booleanList2byte(List<Boolean> a) {
        byte b = 0;
        for(int i = 0; i <= 7 ; i ++ ) {
            if(a.get(i)) {
                int c = (1 << (7-i));
                b += c;
            }
        }
        return b;
    }

    /**
     * Writes the byte Stream of Label Range to channel buffer.
     *
     * @param c of type channel buffer
     */
    public int write(ChannelBuffer c) {
        int iLenStartIndex = c.writerIndex();
        c.writeByte(ACTION);
        c.writeByte(NUMLABELS);
        c.writeShort(LENGTH);
        c.writeByte(GRID_CS);
        c.writeByte(RESERVED);
        c.writeShort(LOWEST_FREQUENCY);
        for (int i = 0; i < labelList.size(); i = i + 8) {
            List<Boolean> subLabels = labelList.subList(i, (i+8));
            byte labels = booleanList2byte(subLabels);
            c.writeByte(labels);
        }
        c.writeByte(PADDING1);
        c.writeShort(PADDING2);
        return c.writerIndex() - iLenStartIndex;
    }

    byte name(List<Boolean> param) {
        byte rtn = 0x00;
        if (param.size() ==8 ) {
            int i=0;
            for (; i< param.size(); i++) {
                Boolean m  = (Boolean) param.get(i);
                byte a = 0x01;
                if (m.booleanValue()) {
                    a = (byte) (a << (7-i));
                    rtn = (byte) (rtn | a);
                }
            }
        }
        return rtn;
    }
    public List<Boolean> getLabels() {
        return labelList;
    }

    /**
     * returns the length of (Label Set Field + MaxLabelRange) in bytes.
     * @return The length of (Label Set Field + MaxLabelRange) in bytes.
     */
    public int length() {
        return LENGTH;
    }

    public static boolean[] print(boolean[] a, boolean[] b) {
        boolean[] c = new boolean[a.length + b.length];
        System.arraycopy(a, 0 ,c, 0, a.length);
        System.arraycopy(a, 0, c, a.length, b.length);
        a = c;
        return a;
    }
    /**
     * Reads from channel buffer and returns object of InternalLabelRange.
     * @param c input channel buffer
     * @return object of InternalLabelRange
     * @throws PcepParseException pcep parse exception.
     */
    public static InternalLabelRange read(ChannelBuffer c, int length) throws PcepParseException {
        if (length == LENGTH) {
            c.readByte();
            int num = c.readByte();
            c.readShort();
            c.readInt();
            List labelList = new ArrayList();
            for (int i = 0; i < num / 8; i ++) {
                byte label = c.readByte();
                boolean[] labelBool = byte2boolean(label);
                for (int j = 0; j < 8; j ++) {
                    labelList.add(labelBool[i]);
                }
            }
            c.readByte();
            c.readShort();

            return new InternalLabelRange(labelList);

        } else {
            throw new PcepParseException(
                    "The length can not match Restriction Parameters in Restriction Type equals 2");
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InternalLabelRange that = (InternalLabelRange) o;

        if (LOWEST_FREQUENCY != that.LOWEST_FREQUENCY) return false;
        return labelList.equals(that.labelList);

    }

    public int hashCode() {
        int result = (int) LOWEST_FREQUENCY;
        result = 31 * result + labelList.hashCode();
        return result;
    }

    public String toString() {
        return "InternalLabelRange{" +
                "LOWEST_FREQUENCY=" + LOWEST_FREQUENCY +
                ", labelList=" + labelList +
                '}';
    }
}
