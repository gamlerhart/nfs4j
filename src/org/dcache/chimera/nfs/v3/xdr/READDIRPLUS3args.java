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
package org.dcache.chimera.nfs.v3.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class READDIRPLUS3args implements XdrAble {
    public nfs_fh3 dir;
    public cookie3 cookie;
    public cookieverf3 cookieverf;
    public count3 dircount;
    public count3 maxcount;

    public READDIRPLUS3args() {
    }

    public READDIRPLUS3args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        dir.xdrEncode(xdr);
        cookie.xdrEncode(xdr);
        cookieverf.xdrEncode(xdr);
        dircount.xdrEncode(xdr);
        maxcount.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        dir = new nfs_fh3(xdr);
        cookie = new cookie3(xdr);
        cookieverf = new cookieverf3(xdr);
        dircount = new count3(xdr);
        maxcount = new count3(xdr);
    }

}
// End of READDIRPLUS3args.java
