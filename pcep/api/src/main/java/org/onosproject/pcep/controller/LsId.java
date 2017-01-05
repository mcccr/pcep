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

package org.onosproject.pcep.controller;

import org.onlab.util.HexString;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;
import static org.onlab.util.Tools.fromHex;
import static org.onlab.util.Tools.toHex;

/**
 * LS-ID advertises a device or link.
 */
public final class LsId {

    // must be "pcep", because its scheme must match pcep ptorocol and provider.
    private static final String SCHEME = "pcep";
    private static final long UNKNOWN = 0;
    private final long value;

    /**
     * Default constructor.
     */
    public LsId() {
        this.value = LsId.UNKNOWN;
    }

    /**
     * Constructor from a long value.
     *
     * @param value the value to use.
     */
    public LsId(long value) {
        this.value = value;
    }

    /**
     * Constructor from a string.
     *
     * @param value the value to use.
     */
    public LsId(String value) {
        this.value = HexString.toLong(value);
    }

    /**
     * Get the value of the LSID.
     *
     * @return the value of the LSID.
     */
    public long value() {
        return value;
    }

    /**
     * Convert the LSID value to a ':' separated hexadecimal string.
     *
     * @return the LSID value as a ':' separated hexadecimal string.
     */
    @Override
    public String toString() {
        return int2ip((int) this.value);
//        return HexString.toHexString(this.value);
    }


    private static String int2ip(int ipInt) {
        return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.')
                .append((ipInt >> 16) & 0xff).append('.').append(
                        (ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff))
                .toString();
    }


    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LsId)) {
            return false;
        }

        LsId otherLsId = (LsId) other;

        return value == otherLsId.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    /**
     * Returns LSID created from the given device URI.
     *
     * @param uri device URI
     * @return lsId
     */
    public static LsId lsId(URI uri) {
        checkArgument(uri.getScheme().equals(SCHEME), "Unsupported URI scheme");
        return new LsId(fromHex(uri.getSchemeSpecificPart()));
    }

    /**
     * Produces device URI from the given LSID.
     *
     * @param lsId device lsId
     * @return device URI
     */
    public static URI uri(LsId lsId) {
        return uri(lsId.value);
    }

    /**
     * Produces device URI from the given LSID long.
     *
     * @param value device lsId as long
     * @return device URI
     */
    public static URI uri(long value) {
        try {
//            return new URI(SCHEME, toHex(value), null);
            return new URI(SCHEME, int2ip((int) value), null);
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
