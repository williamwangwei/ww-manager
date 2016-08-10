package william.wangwei.quartz;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class SimpleSchedule01 {

	public static void main(String[] args) {
		//通过SchedulerFactory来获取一个调度器  
		 SchedulerFactory factory=new StdSchedulerFactory();
	        try {
	            Scheduler scheduler = factory.getScheduler();
	            scheduler.start();

	            //引进作业程序public JobDetail(String jobName, String jobGroupName, Class jobClass){}  
	            //JobDetail jobDetail = new JobDetailImpl("SimpleJob",null, SimpleJob.class);
	            JobDetail jobDetail = new JobDetailImpl("SimpleJob", null,  SimpleJob.class);
	            
	            /*new一个触发器public SimpleTrigger(String triggerName, String triggerGroupName){} 
	            在构造Trigger实例时，可以考虑使用org.quartz.TriggerUtils工具类，该工具类不但提供了众多获取特定时间的方法，还拥有众多获取常见Trigger的方法， 
	            如makeSecondlyTrigger(String trigName)方法将创建一个每秒执行一次的Trigger，而makeWeeklyTrigger(String trigName, int dayOfWeek, int hour, int minute) 
	            将创建一个每星期某一特定时间点执行一次的Trigger。而getEvenMinuteDate(Date date)方法将返回某一时间点一分钟以后的时间。*/ 
	            
	            /*Trigger simplerTrigger = TriggerUtils.makeSecondlyTrigger(10);
	            simplerTrigger.setName("SimpleTrigger");*/
	            SimpleTrigger simpleTrigger = new SimpleTriggerImpl("simpleTrigger", "triggerGroup-tg1");
	            
	          //设置作业启动时间,马上启动  
	            long ctime = System.currentTimeMillis();  
	            //simpleTrigger.
	           /* simpleTrigger.setStartTime(new Date(ctime));  
	            //设置作业执行间隔   
	            simpleTrigger.setRepeatInterval(1000);  
	            //设置作业执行次数  
	            simpleTrigger.setRepeatCount(10);  
	            //设置作业执行优先级默认为5  
	            simpleTrigger.setPriority(10); 
	            //作业和触发器注册到调度器中,并建立Trigger和JobDetail的关联  
	            scheduler.scheduleJob(jobDetail, simpleTrigger);  */ 
	            /*上面的一句话，也可以换成下面的这几句话 
	             * JobDetail jobDetail = new JobDetail("job1_1","jGroup1", SimpleJob.class); 
	            SimpleTrigger simpleTrigger = new SimpleTrigger("trigger1_1","tgroup1"); 
	            simpleTrigger.setJobGroup("jGroup1");：指定关联的Job组名 
	            simpleTrigger.setJobName("job1_1");指定关联的Job名称 
	            scheduler.addJob(jobDetail, true);注册JobDetail 
	            scheduler.scheduleJob(simpleTrigger);注册指定了关联JobDetail的Trigger*/  
	            //启动调度器  
	            //scheduler.start();  
	            //scheduler.scheduleJob(jobDetail, simplerTrigger);
	        }catch (SchedulerException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	}

}
