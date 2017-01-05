/*
 * Copyright 2015-present Open Networking Laboratory
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
package org.onosproject.pcep.controller;

import org.onlab.packet.IpAddress;
import org.onlab.util.Identifier;

import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The class representing a network client pc ip.
 * This class is immutable.
 */
public final class PccId extends Identifier<IpAddress> {

    private static final String SCHEME = "l3";
    private static final long UNKNOWN = 0;
    private IpAddress ipAddress;
    private int port;

    /**
     * Private constructor.
     */
    private PccId(IpAddress ipAddress, int port) {
//        super(ipAddress);
        this.ipAddress = ipAddress;
        this.port = port;


    }

    /**
     * Create a PccId from ip address.
     *
     * @param ipAddress IP address
     * @return ipAddress
     */
    public static PccId pccId(IpAddress ipAddress, int port) {
        return new PccId(ipAddress, port);
    }

    /**
     * Returns the ip address.
     *
     * @return ipAddress
     */
    public IpAddress ipAddress() {
        return identifier;
    }

    /**
     * Returns PccId created from the given client URI.
     *
     * @param uri device URI
     * @return pccid
     */
    public static PccId pccid(URI uri) {
        checkArgument(uri.getScheme().equals(SCHEME), "Unsupported URI scheme");
        return new PccId(IpAddress.valueOf(uri.getSchemeSpecificPart()), uri.getPort());
    }

    /**
     * Produces client URI from the given DPID.
     *
     * @param pccid client pccid
     * @return client URI
     */
    public static URI uri(PccId pccid) {
        return uri(pccid.ipAddress());
    }

    /**
     * Produces client URI from the given ip address.
     *
     * @param ipAddress ip of client
     * @return client URI
     */
    public static URI uri(IpAddress ipAddress) {
        try {
            return new URI(SCHEME, ipAddress.toString(), null);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PccId pccId = (PccId) o;

        if (port != pccId.port) return false;
        return ipAddress != null ? ipAddress.equals(pccId.ipAddress) : pccId.ipAddress == null;

    }

    @Override
    public int hashCode() {
        int result = ipAddress.hashCode();
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "PccId{" +
                "ipAddress=" + ipAddress +
                ", port=" + port +
                '}';
    }
}
