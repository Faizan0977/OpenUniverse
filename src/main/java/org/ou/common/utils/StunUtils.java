package org.ou.common.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.socket.IceUdpSocketWrapper;
import org.ice4j.stunclient.SimpleAddressDetector;

public class StunUtils {

    /**
     * Returns all local (host) IP addresses (IPv4 + IPv6) except loopback.
     */
    public static Collection<String> getLocalAddresses() throws SocketException {
        List<String> result = new ArrayList<>();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface ni : Collections.list(interfaces)) {
            if (!ni.isUp() || ni.isLoopback()) {
                continue;
            }

            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            for (InetAddress addr : Collections.list(addresses)) {
                result.add(addr.getHostAddress());
            }
        }
        return result;
    }

    /**
     * Returns public/global addresses: - Uses STUN to discover public IPv4 (if
     * available). - Includes global IPv6 addresses (excluding
     * link-local/unique-local).
     */
    public static Collection<String> getGlobalAddresses(InetSocketAddress stunInetSocketAddress) throws SocketException, IOException {
        List<String> result = new ArrayList<>();

        TransportAddress stunServer = new TransportAddress(stunInetSocketAddress, Transport.UDP);
        SimpleAddressDetector detector = new SimpleAddressDetector(stunServer);
        detector.start();

        DatagramSocket socket = new DatagramSocket(0);
        IceUdpSocketWrapper iceSocket = new IceUdpSocketWrapper(socket);
        TransportAddress publicIPv4 = detector.getMappingFor(iceSocket);

        socket.close();
        detector.shutDown();

        if (publicIPv4 != null) {
            result.add(publicIPv4.getAddress().getHostAddress());
        }

        // Global IPv6
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface ni : Collections.list(interfaces)) {
            if (!ni.isUp() || ni.isLoopback()) {
                continue;
            }
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            for (InetAddress addr : Collections.list(addresses)) {
                if (addr instanceof Inet6Address) {
                    String s = addr.getHostAddress();
                    // Exclude link-local (fe80::/10) and unique-local (fc00::/7)
                    if (!s.startsWith("fe80") && !s.startsWith("fc") && !s.startsWith("fd")) {
                        result.add(s);
                    }
                }
            }
        }
        return result;
    }

    // public static void main(String[] args) throws Exception {
    //     System.out.println("Local addresses:");
    //     getLocalAddresses().forEach(System.out::println);

    //     String stunArg = "stun.l.google.com:19302";
    //     InetSocketAddress stunAddress = CommonUtils.parseHostPort(stunArg);

    //     System.out.println("\nGlobal addresses:");
    //     getGlobalAddresses(stunAddress).forEach(System.out::println);
    // }
}
