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

import sarah.Scheduler;
import sarah.operations.Abort;
import sarah.operations.Commit;
import sarah.operations.Read;
import sarah.operations.Write;

/**
 * The class {@code SGTScheduler} is an implementation of a serialization 
 * graph scheduler. 
 */
public class SGTScheduler extends Scheduler {
	

	/**
	 * Creates a new {@link SGTScheduler}.  
	 */
	public SGTScheduler() {

	}
	
	/* (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Read)
	 */
	@Override
	public void visit(Read read) {
		
	}

	/* (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Write)
	 */
	@Override
	public void visit(Write write) {
		
	}

	/* (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Abort)
	 */
	@Override
	public void visit(Abort abort) {
		
	}

	/* (non-Javadoc)
	 * @see sarah.operations.OperationVisitor#visit(sarah.operations.Commit)
	 */
	@Override
	public void visit(Commit commit) {
		
	}
}
