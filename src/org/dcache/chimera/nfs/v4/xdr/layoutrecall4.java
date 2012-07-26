/*
 * Copyright (c) 2009 - 2012 Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class layoutrecall4 implements XdrAble {
    public int lor_recalltype;
    public layoutrecall_file4 lor_layout;
    public fsid4 lor_fsid;

    public layoutrecall4() {
    }

    public layoutrecall4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(lor_recalltype);
        switch ( lor_recalltype ) {
        case layoutrecall_type4.LAYOUTRECALL4_FILE:
            lor_layout.xdrEncode(xdr);
            break;
        case layoutrecall_type4.LAYOUTRECALL4_FSID:
            lor_fsid.xdrEncode(xdr);
            break;
        case layoutrecall_type4.LAYOUTRECALL4_ALL:
            break;
        }
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        lor_recalltype = xdr.xdrDecodeInt();
        switch ( lor_recalltype ) {
        case layoutrecall_type4.LAYOUTRECALL4_FILE:
            lor_layout = new layoutrecall_file4(xdr);
            break;
        case layoutrecall_type4.LAYOUTRECALL4_FSID:
            lor_fsid = new fsid4(xdr);
            break;
        case layoutrecall_type4.LAYOUTRECALL4_ALL:
            break;
        }
    }

}
// End of layoutrecall4.java
