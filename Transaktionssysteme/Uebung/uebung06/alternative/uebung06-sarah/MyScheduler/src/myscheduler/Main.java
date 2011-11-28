package myscheduler;

import sarah.Sarah;
import sarah.Scheduler;
import sarah.SchedulerRegister;

/**
 * Runs Sarah with a given set of {@link Scheduler}s.
 * @author $LastChangedBy: ehlers $
 * @version $Revision: 116 $
 * @version $LastChangedDate: 2009-12-01 17:00:29 +0100 (Di, 01 Dez 2009) $
 * @since 1.0
 */
public final class Main {
	
	/**
	 * Runs Sarah with a given set of {@link Scheduler}s.
	 * @param args Given parameters are ignored.
	 */
	public static void main(String[] args) {

		// register custom schedulers
		SchedulerRegister.register(
				"Two-phase Locking (2PL)", 
				TwoPLScheduler.class);
		SchedulerRegister.register(
				"Strict Two-phase Locking (S2PL)", 
				S2PLScheduler.class);
		SchedulerRegister.register(
				"Strong Strict Two-phase Locking (SS2PL)", 
				SS2PLScheduler.class);
		SchedulerRegister.register(
				"Basic Timestamp Ordering (BTO)", 
				BTOScheduler.class);
		SchedulerRegister.register(
				"Strict Timestamp Ordering (STO)", 
				STOScheduler.class);
		SchedulerRegister.register(
				"Serialization Graph Tester (SGT)",
				SGTScheduler.class);
		
		// start Sarah
		(new Sarah()).start();
	}
}
