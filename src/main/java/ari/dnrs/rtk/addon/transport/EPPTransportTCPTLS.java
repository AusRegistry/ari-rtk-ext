/*
 **
 ** EPP RTK Java
 ** Copyright (C) 2001-2002, Tucows, Inc.
 ** Copyright (C) 2003, Liberty RMS
 **
 **
 ** This library is free software; you can redistribute it and/or
 ** modify it under the terms of the GNU Lesser General Public
 ** License as published by the Free Software Foundation; either
 ** version 2.1 of the License, or (at your option) any later version.
 **
 ** This library is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 ** Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public
 ** License along with this library; if not, write to the Free Software
 ** Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **
 */

/*
 * $Header: /cvsroot/epp-rtk/epp-rtk/java/src/com/tucows/oxrs/epprtk/rtk/transport/EPPTransportTCPTLS.java,v 1.1 2004/12/07 15:53:27 ewang2004 Exp $
 * $Revision: 1.1 $
 * $Date: 2004/12/07 15:53:27 $
 */

package ari.dnrs.rtk.addon.transport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.tucows.oxrs.epprtk.rtk.transport.EPPTransportException;

/**
 * Provides methods necessary to build connection with EPP Server using a SSL-TLS socket. It only overrides the
 * connect() method from EPPTransportTCP since the remaining socket operations are identical to unencrypted sockets.
 * <P>
 * This class uses Sun's JSSE to establish a secure connection with the server. It makes use of the RTK property
 * "ssl.props.location" to retrieve the ssl.properties. This properties file contains information necessary to locate
 * the java keystore, the names of the keys and certificates contained inside it and the necessary passwords to access
 * the keystore. The epp-rtk/java/ssl director contains more information regarding the data required to create a
 * keystore and the steps to do so.
 * <P>
 * If the user does not wish to use the default Sun JSSE but rather a third party SSL implemention, then the socket
 * connection should be established externally and an instance of EPPTransportTCP should be created using the connected
 * socket.
 * <P>
 * Please see the EPP RTK User's Guide for more information on secure connections to EPP servers.
 */
public class EPPTransportTCPTLS extends EPPTransportTCP {

    private SSLContext ctx_ = null;
    private KeyStore ks_ = null;
    private KeyManagerFactory kmf_ = null;
    private SecureRandom rnd_ = null;

    public EPPTransportTCPTLS() {
        super();
    }

    /**
     * Construtor with Hostname, Host port and timeout value
     *
     * @param host_name
     *            The server Hostname
     * @param host_port
     *            The server Host port
     * @param timeout
     *            The int socket timeout value, in milliseconds
     */
    public EPPTransportTCPTLS(String host_name, int host_port, int timeout) {
        super(host_name, host_port, timeout);
    }

    /**
     * Connects to the Server using previously set Hostname and port. If connection has been already established, the
     * operation will be ignored. The method also sets the SO timeout.
     *
     * @throws SocketException
     * @throws IOException
     * @throws UnknownHostException
     */
    @Override
    public void connect() throws SocketException, IOException, UnknownHostException, EPPTransportException {
        String method_name = "connect()";

        debug(DEBUG_LEVEL_THREE, method_name, "Entered");

        if (!preset_) {
            // Initialize to null the socket to the server
            socket_to_server_ = null;

            debug(DEBUG_LEVEL_TWO, method_name, "Using SSL/TLS");

            Properties system_props = System.getProperties();
            String ssl_props_location = system_props.getProperty("ssl.props.location");
            if (ssl_props_location == null || ssl_props_location.length() == 0) {
                throw new IOException("No ssl props location specified");
            }
            Properties ssl_props = new Properties();
            ssl_props.load(new FileInputStream(ssl_props_location + File.separator + "ssl.properties"));

            SSLSocketFactory ssl_factory;
            try {
                ssl_factory = getSSLFactory(method_name, ssl_props_location, ssl_props);
                socket_to_server_ = ssl_factory.createSocket(epp_host_name_, epp_host_port_);
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException(e.getLocalizedMessage());
            }

            // Force the handshake to happen now so we can check for a good connection
            SSLSession la_session = ((SSLSocket) socket_to_server_).getSession();
            if (socket_to_server_ == null || la_session.getProtocol().equals("NONE")) {
                throw new EPPTransportException(
                        "Failed to establish secure connection to server.  Perhaps a bad certificate?"
                                + " -- use -Djavax.net.debug=all to see errors.");
            }
        }

        socket_to_server_.setSoTimeout(epp_timeout_);

        reader_from_server_ = new BufferedInputStream(socket_to_server_.getInputStream());
        writer_to_server_ = new BufferedOutputStream(socket_to_server_.getOutputStream());

        debug(DEBUG_LEVEL_TWO, method_name, "Connected to [" + socket_to_server_.getInetAddress() + ":"
                + socket_to_server_.getPort() + "]");

        debug(DEBUG_LEVEL_THREE, method_name, "Leaving");

        return;
    }

    private SSLSocketFactory getSSLFactory(String method_name, String ssl_props_location, Properties ssl_props)
            throws IOException, KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException, KeyManagementException {
        SSLSocketFactory ssl_factory = null;
        char[] passphrase1 = ((String) ssl_props.get("ssl.keystore.passphrase")).toCharArray();
        char[] passphrase2 = ((String) ssl_props.get("ssl.signedcert.passphrase")).toCharArray();

        initKeyStore(ssl_props_location, ssl_props, passphrase1);

        initKeyManagers(ssl_props, passphrase2);

        initSSLContext(ssl_props_location, ssl_props);

        ssl_factory = ctx_.getSocketFactory();
        return ssl_factory;
    }

    private void initSecureRandonNumber(String method_name) {
        // SSL Performance improvement from wessorh
        try {
            byte seed[] = new byte[1024];
            FileInputStream is = new FileInputStream("/dev/urandom");
            is.read(seed);
            is.close();

            rnd_ = java.security.SecureRandom.getInstance("SHA1PRNG");
            rnd_.setSeed(seed);
            debug(DEBUG_LEVEL_TWO, method_name, "SecureRandom seed set.");

        } catch (Exception xcp) {
            debug(DEBUG_LEVEL_TWO, method_name, "Error initializing SecureRandom [" + xcp.getMessage()
                    + "], using default initialization.");
            rnd_ = null;
        }
    }

    private void initKeyManagers(Properties ssl_props, char[] passphrase2) throws NoSuchAlgorithmException,
            KeyStoreException, UnrecoverableKeyException {
        if (kmf_ == null) {
            kmf_ = KeyManagerFactory.getInstance(((String) ssl_props.get("ssl.keymanagerfactory.format")));
            kmf_.init(ks_, passphrase2);
        }
    }

    private void initKeyStore(String ssl_props_location, Properties ssl_props, char[] passphrase1)
            throws KeyStoreException, NoSuchProviderException, IOException, NoSuchAlgorithmException,
            CertificateException, FileNotFoundException {
        if (ks_ == null) {
            if (ssl_props.get("ssl.keystore.provider") == null) {
                ks_ = KeyStore.getInstance((String) ssl_props.get("ssl.keystore.format"));
            } else {
                ks_ = KeyStore.getInstance((String) ssl_props.get("ssl.keystore.format"),
                        (String) ssl_props.get("ssl.keystore.provider"));
            }
            ks_.load(
                    new FileInputStream(ssl_props_location + File.separator
                            + ((String) ssl_props.get("ssl.keystore.file"))), passphrase1);
        }
    }

    private void initSSLContext(String ssl_props_location, Properties ssl_props) throws NoSuchAlgorithmException,
            KeyManagementException, CertificateExpiredException, CertificateNotYetValidException, KeyStoreException,
            CertificateException, IOException {
        if (ctx_ == null) {
            ctx_ = SSLContext.getInstance(((String) ssl_props.get("ssl.protocol")));
        }
        ctx_.init(kmf_.getKeyManagers(), loadTrustManagers(ssl_props_location, ssl_props), rnd_);
    }

    private TrustManager[] loadTrustManagers(String ssl_props_location, final Properties ssl_props)
            throws CertificateExpiredException, CertificateNotYetValidException, KeyStoreException,
            CertificateException, NoSuchAlgorithmException, IOException {

        final String trustStoreFilename = (String) ssl_props.get("ssl.truststore.location");
        if (trustStoreFilename != null) {
            rnd_ = null;
            final char[] passphrase = ((String) ssl_props.get("ssl.truststore.pass")).toCharArray();

            KeyStore trustStore = loadKeystore(ssl_props_location + File.separator + trustStoreFilename, passphrase,
                    KeyStore.getDefaultType());
            TrustManagerFactory tmf = null;
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            return tmf.getTrustManagers();
        } else {
            initSecureRandonNumber("loadTrustManagers");
        }
        return null;
    }

    private KeyStore loadKeystore(String filename, char[] passphrase, String type) throws KeyStoreException,
            CertificateException, CertificateExpiredException, CertificateNotYetValidException,
            NoSuchAlgorithmException, IOException {
        KeyStore store = null;

        try {
            store = KeyStore.getInstance(type);
        } catch (KeyStoreException kse) {
            if (type.equals(KeyStore.getDefaultType())) {
                kse.printStackTrace();
                throw kse;
            } else {
                try {
                    store = KeyStore.getInstance(KeyStore.getDefaultType());
                } catch (KeyStoreException ksx) {
                    ksx.printStackTrace();
                    throw ksx;
                }
            }
        }

        InputStream in = null;
        try {
            in = new FileInputStream(filename);
            store.load(in, passphrase);
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return store;
    }
}
