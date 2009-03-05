package org.dcache.xdr;

import com.sun.grizzly.ProtocolParser;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.logging.Logger;
import java.util.logging.Level;

public class RpcProtocolPaser implements ProtocolParser<Xdr> {

    private final static Logger _log = Logger.getLogger(RpcProtocolPaser.class.getName());
    private final static int MAX_XDR_SIZE = 128 * 1024;
    /*
     * RPC: 1831
     *     Remote Procedure Call Protocol Specification Version 2
     *
     * When RPC messages are passed on top of a byte stream transport
     * protocol (like TCP), it is necessary to delimit one message from
     * another in order to detect and possibly recover from protocol errors.
     * This is called record marking (RM).  One RPC message fits into one RM
     * record.
     *
     * A record is composed of one or more record fragments.  A record
     * fragment is a four-byte header followed by 0 to (2**31) - 1 bytes of
     * fragment data.  The bytes encode an unsigned binary number; as with
     * XDR integers, the byte order is from highest to lowest.  The number
     * encodes two values -- a boolean which indicates whether the fragment
     * is the last fragment of the record (bit value 1 implies the fragment
     * is the last fragment) and a 31-bit unsigned binary value which is the
     * length in bytes of the fragment's data.  The boolean value is the
     * highest-order bit of the header; the length is the 31 low-order bits.
     *
     */
    /** RPC fragment recodr marker mask */
    private final static int RPC_LAST_FRAG = 0x80000000;
    /** RPC fragment size mask */
    private final static int RPC_SIZE_MASK = 0x7fffffff;
    /**
     * Xdr which we try to construct.
     */
    private Xdr _xdr = null;
    private boolean _lastFragment = false;
    private int _fragmentToRead = 0;
    /** position within buffer */
    private int _nextMessageStartPosition = 0;
    /** */
    private boolean _isMuti = false;
    private ByteBuffer _buffer = null;
    private boolean _expectingMoreData;

    public RpcProtocolPaser() {
        _log.log(Level.FINEST, "new instance created");
    }

    /**
     *
     * @see com.sun.grizzly.ProtocolParser#isExpectingMoreData()
     */
    @Override
    public boolean isExpectingMoreData() {
        _log.log(Level.FINEST, "isExpectingMoreData {0}", _expectingMoreData);
        return _expectingMoreData;
    }

    /**
     *
     * @see com.sun.grizzly.ProtocolParser#hasMoreBytesToParse()
     */
    @Override
    public boolean hasMoreBytesToParse() {
        
        boolean rc = _buffer != null &&
            !_expectingMoreData &&
            _buffer.position() > _nextMessageStartPosition;
        
        _log.log(Level.FINEST, "hasMoreBytesToParse {0}", rc);
        return rc;
    }

    /**
     *
     * @see com.sun.grizzly.ProtocolParser#getNextMessage()
     */
    @Override
    public Xdr getNextMessage() {
        _lastFragment = false;
        _fragmentToRead = 0;
        _isMuti = false;
        Xdr xdr = _xdr;
        _xdr = null;
        return xdr;
    }

    /**
     *
     * @see com.sun.grizzly.ProtocolParser#hasNextMessage()
     */
    @Override
    public boolean hasNextMessage() {

        /*
         * do we have some data to process?
         */
        if (_buffer == null) {
            _log.log(Level.FINEST, "hasNextMessage false");
            return false;
        }

        /*
         * It may happen that single buffer will contain multiple fragments.
         * Loop over the buffer content till we get complete message or buffer
         * has no more data.
         */
        _expectingMoreData = true;
        ByteBuffer bytes = _buffer.duplicate();
        bytes.position(_nextMessageStartPosition);
        bytes.order(ByteOrder.BIG_ENDIAN);

        while (_expectingMoreData ) {

            /*
             * do not go more that available data
             */
            bytes.limit( _buffer.position() );

            if( ! bytes.hasRemaining() ) break;

            if (_fragmentToRead == 0) {

                /*
                 * if it's a beginnig of a message, do we have at least 4 bytes
                 * for message size
                 */
                if (_xdr == null && bytes.remaining() < 4) {
                    _log.log(Level.FINEST, "hasNextMessage false");
                    return false;
                }

                if (_xdr == null) {
                    _xdr = new Xdr(MAX_XDR_SIZE);
                }

                _fragmentToRead = bytes.getInt();
                _nextMessageStartPosition += 4;
                _lastFragment = (_fragmentToRead & RPC_LAST_FRAG) != 0;
                _fragmentToRead &= RPC_SIZE_MASK;
                if (_lastFragment) {
                    if (_isMuti) {
                        _log.log(Level.INFO, "Multifragment XDR END");
                    }
                } else {
                    _isMuti = true;
                    _log.log(Level.INFO, "Multifragment XDR, expected len {0}, available {1}",
                            new Object[]{_fragmentToRead, bytes.remaining()});
                }
            }

            int n = Math.min(_fragmentToRead, bytes.remaining());
            _nextMessageStartPosition += n;
            
            bytes.limit(bytes.position() + n);
            _xdr.fill(bytes);

            _fragmentToRead -= n;
            _expectingMoreData = !(_fragmentToRead == 0 && _lastFragment);
        }

        if (_isMuti) {
            _log.log(Level.INFO, "Multifragment XDR, remaining {0} last: {1}",
                    new Object[]{_fragmentToRead, _lastFragment});
        }

        _log.log(Level.FINEST, "hasNextMessage {0}", !_expectingMoreData);

        return !_expectingMoreData;
    }

    /**
     *
     * @see com.sun.grizzly.ProtocolParser#startBuffer(java.nio.ByteBuffer buffer)
     */
    @Override
    public void startBuffer(ByteBuffer buffer) {
        _log.log(Level.FINEST, "startBuffer");
        _buffer = buffer;
        _buffer.order(ByteOrder.BIG_ENDIAN);
    }

    /**
     *
     * @see com.sun.grizzly.ProtocolParser#releaseBuffer()
     */
    @Override
    public boolean releaseBuffer() {
        _log.log(Level.FINEST, "releaseBuffer");
        if ( !hasMoreBytesToParse() ) {
            _nextMessageStartPosition = 0;
            _buffer.clear();
            _buffer = null;
        }        
        return _expectingMoreData;
    }

    @Override
    public String toString() {

        String str = String.format("hasMoreBytesToParse %s, expectingMoreData %s, pos %d, nextp %d",
            hasMoreBytesToParse(),
            _expectingMoreData,
            _buffer == null ? -1: _buffer.position(),
            _nextMessageStartPosition);

        return str;

    }
}
