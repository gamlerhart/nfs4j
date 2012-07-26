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
package org.dcache.chimera.nfs.v4.client;

import java.nio.ByteBuffer;

import org.dcache.chimera.nfs.v4.xdr.WRITE4args;
import org.dcache.chimera.nfs.v4.xdr.nfs_argop4;
import org.dcache.chimera.nfs.v4.xdr.nfs_opnum4;
import org.dcache.chimera.nfs.v4.xdr.offset4;
import org.dcache.chimera.nfs.v4.xdr.stable_how4;
import org.dcache.chimera.nfs.v4.xdr.stateid4;
import org.dcache.chimera.nfs.v4.xdr.uint64_t;

public class WriteStub {

    public static nfs_argop4 generateRequest(long offset, byte[] data, stateid4 stateid) {

        WRITE4args args = new WRITE4args();

        args.stable = stable_how4.FILE_SYNC4;

        args.offset = new offset4(new uint64_t(offset));

        args.stateid = stateid;

        args.data = ByteBuffer.wrap(data);
        args.data.position(data.length);

        nfs_argop4 op = new nfs_argop4();
        op.argop = nfs_opnum4.OP_WRITE;
        op.opwrite = args;

        return op;
    }
}
