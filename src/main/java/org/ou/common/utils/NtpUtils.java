package org.ou.common.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class NtpUtils {

    public static String getUtcIsoTime(NTPUDPClient client, InetSocketAddress inetSocketAddress) throws Exception {
            int port = inetSocketAddress.getPort();
            String host = inetSocketAddress.getHostString();
            InetAddress hostAddr = InetAddress.getByName(host);
            TimeInfo info = port == 0 ? client.getTime(hostAddr) : client.getTime(hostAddr, port);
            Instant instant = Instant.ofEpochMilli(info.getMessage()
                                                     .getTransmitTimeStamp()
                                                     .getTime());
            return DateTimeFormatter.ISO_INSTANT
                    .withZone(ZoneOffset.UTC)
                    .format(instant);
    }

}
