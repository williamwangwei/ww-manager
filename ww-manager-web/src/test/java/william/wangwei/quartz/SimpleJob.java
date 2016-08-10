package william.wangwei.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class SimpleJob implements Job {

	@Override
	public void execute(JobExecutionContext ctx) {
		// TODO Auto-generated method stub
		 System.out.println("[JOB] Welcome to Quartz!");
	}

}
