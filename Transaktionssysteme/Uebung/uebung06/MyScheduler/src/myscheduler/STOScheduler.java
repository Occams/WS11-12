/**
 * Sarah - A Vi(s)u(a)lizer fo(r) Dat(a)base Sc(h)edulers
 * 
 * Copyright (c) 2009 The Sarah Team
 *
 * All Rights Reserved.
 *
 * This file is part of the project "MyScheduler" of Sarah.
 *
 * Sarah is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; see the file LICENSE.
 * If not you can find the GPL at http://www.gnu.org/copyleft/gpl.html
 */
package myscheduler;

import static sarah.operations.LockType.READ;
import static sarah.operations.LockType.WRITE;

import com.google.common.base.Preconditions;

import sarah.Scheduler;
import sarah.data.Data;
import sarah.operations.Abort;
import sarah.operations.Commit;
import sarah.operations.Read;
import sarah.operations.Write;

/**
 * The class {@link STOScheduler} is an implementation of a STO (strict
 * timestamp ordering) scheduler.
 * @author $LastChangedBy: ehlers $
 * @version $Revision: 123 $
 * @version $LastChangedDate: 2009-12-02 11:10:34 +0100 (Mi, 02 Dez 2009) $
 * @since 1.0
 */
public final class STOScheduler extends Scheduler {
	
	/* (non-Javadoc)
	 * @see myscheduler.operations.OperationVisitor#visit(
	 * myscheduler.operations.Read)
	 */
	public final void visit(Read read) {
		Preconditions.checkNotNull(read);
		
		Data data = read.getData();
		long readTS = getTimestamp(data, READ);
		long writeTS = getTimestamp(data, WRITE);
		long ts = read.getTransaction();
		if (writeTS <= ts) {
			if ((writeTS == ts) 
					|| isCommitted(writeTS) || isRejected(writeTS)) {
				setTimestamp(data, READ, Math.max(readTS, ts));
				output(read);
			} else {
				block(read);
			}
		} else {
			reject(read);
		}
	}
	
	/* (non-Javadoc)
	 * @see myscheduler.operations.OperationVisitor#visit(
	 * myscheduler.operations.Write)
	 */
	public final void visit(Write write) {
		Preconditions.checkNotNull(write);
		
		Data data = write.getData();
		long readTS = getTimestamp(data, READ);
		long writeTS = getTimestamp(data, WRITE);
		long ts = write.getTransaction();
		if ((readTS <= ts) && (writeTS <= ts)) {
			if ((writeTS == ts) 
					|| isCommitted(writeTS) || isRejected(writeTS)) {
				setTimestamp(data, WRITE, ts);
				output(write);	
			} else {
				block(write);
			}
		} else {
			reject(write);
		}
	}
	
	/* (non-Javadoc)
	 * @see myscheduler.operations.OperationVisitor#visit(
	 * myscheduler.operations.Abort)
	 */
	public final void visit(Abort abort) {
		Preconditions.checkNotNull(abort);
		
		output(abort);
		unblockAll();
	}
	
	/* (non-Javadoc)
	 * @see myscheduler.operations.OperationVisitor#visit(
	 * myscheduler.operations.Commit)
	 */
	public final void visit(Commit commit) {
		Preconditions.checkNotNull(commit);
		
		output(commit);
		unblockAll();
	}
}