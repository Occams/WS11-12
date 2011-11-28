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
import sarah.operations.Abort;
import sarah.operations.Commit;
import sarah.operations.LockType;
import sarah.operations.Read;
import sarah.operations.Write;

/**
 * The class {@code TwoPLScheduler} is an implementation of a 2PL scheduler.
 * @author $LastChangedBy: ehlers $
 * @version $Revision: 123 $
 * @version $LastChangedDate: 2009-12-02 11:10:34 +0100 (Mi, 02 Dez 2009) $
 * @since 1.0
 */
public final class TwoPLScheduler extends Scheduler {

	/* (non-Javadoc)
	 * @see myscheduler.OperationVisitor#visit(myscheduler.operations.Read)
	 */
	@Override
	public final void visit(Read read) {
		Preconditions.checkNotNull(read);
		
		if (lock(read.getTransaction(), read.getData(), READ)) {
			output(read);
		} else {
			block(read);
		}
	}

	/* (non-Javadoc)
	 * @see myscheduler.OperationVisitor#visit(myscheduler.operations.Write)
	 */
	@Override
	public final void visit(Write write) {
		Preconditions.checkNotNull(write);
		
		if (lock(write.getTransaction(), write.getData(), WRITE)) {
			output(write);
		} else {
			block(write);
		}
	}

	/* (non-Javadoc)
	 * @see myscheduler.OperationVisitor#visit(myscheduler.operations.Abort)
	 */
	@Override
	public final void visit(Abort abort) {
		Preconditions.checkNotNull(abort);
		
		long transaction = abort.getTransaction();
		unlockAll(transaction, READ);
		unlockAll(transaction, WRITE);
		output(abort);
	}

	/* (non-Javadoc)
	 * @see myscheduler.OperationVisitor#visit(myscheduler.operations.Commit)
	 */
	@Override
	public final void visit(Commit commit) {
		Preconditions.checkNotNull(commit);
		
		long transaction = commit.getTransaction();
		unlockAll(transaction, READ);
		unlockAll(transaction, WRITE);
		output(commit);
	}
	
	/* (non-Javadoc)
	 * @see myscheduler.Scheduler#locksCompatible(
	 * myscheduler.LockType, myscheduler.LockType)
	 */
	@Override
	public final boolean lockTypesCompatible(
			LockType lockType1, LockType lockType2) {
		Preconditions.checkNotNull(lockType1);
		Preconditions.checkNotNull(lockType2);
		
		return lockType1.equals(READ) && lockType2.equals(READ);
	}
	
	/* (non-Javadoc)
	 * @see sarah.Scheduler#lockTypeUpgradeable(
	 * sarah.operations.LockType, sarah.operations.LockType)
	 */
	@Override
	public final boolean lockTypeUpgradeable(
			LockType existingLock, LockType requestedLock) {
		Preconditions.checkNotNull(existingLock);
		Preconditions.checkNotNull(requestedLock);
		
		return existingLock.equals(READ) && requestedLock.equals(WRITE);
	}
}
