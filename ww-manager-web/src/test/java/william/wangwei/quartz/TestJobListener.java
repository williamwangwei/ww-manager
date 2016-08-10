package william.wangwei.quartz;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
public class TestJobListener {
	public static Scheduler scheduler;
	 
    @BeforeClass
    public static void setBeforeClass() throws SchedulerException {
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }
 
    @AfterClass
    public static void setAfterClass() throws SchedulerException {
        if (scheduler.isStarted()) {
            scheduler.shutdown(true);
        }
    }
 
    //Prepared for Test
 
    /**
     * 使用Builder模式构建JobDetail实例
     * @return
     */
    public static JobDetail getJobDetail() {
        return JobBuilder.newJob(MyJob.class).withDescription("MyJobDetail")
                .withIdentity("myJob", "myJobGroup")
                .build();
    }
 
    /**
     * 使用Builder模式构建Trigger实例
     * @return
     */
    public static Trigger getTrigger() {
        return TriggerBuilder.newTrigger().withDescription("MyTrigger").withIdentity("myTrigger",
                "myTriggerGroup")
                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(3))
                .startNow().build();
    }
 
    @Test
    public void testOne() throws SchedulerException {
        JobDetail jobDetail = getJobDetail();
        Trigger trigger = getTrigger();
 
        scheduler.getListenerManager().addJobListener(new MyJobListener());
        scheduler.getListenerManager().addTriggerListener(new MyTriggerListener());
 
        scheduler.scheduleJob(jobDetail, trigger);
 
    }
 
    @Test
    public void testTwo() throws SchedulerException {
        JobDetail jobDetail = getJobDetail();
        Trigger trigger = getTrigger();
 
        /**
         * 为指定jobGrup添加JobListener
         */
        scheduler.getListenerManager().addJobListener(new MyJobListener(), GroupMatcher.jobGroupEquals("myJobGroup"));
        /**
         * 为指定triggerGroup添加TriggerListener
         */
        scheduler.getListenerManager().addTriggerListener(new MyTriggerListener(), GroupMatcher.triggerGroupEquals
                ("myTriggerGroup"));
 
        scheduler.scheduleJob(jobDetail, trigger);
    }
 
    @Test
    public void testThree() throws SchedulerException, InterruptedException {
        JobDetail jobDetail = getJobDetail();
 
        //非持久化Job无关联Trigger添加到Scheduler需要使用addJob第三个参数storeNonDurableWhileAwaitingScheduling为true
        scheduler.addJob(jobDetail, false, true);
 
        //JobDetail 1<->* Trigger
        Trigger trigger1 = TriggerBuilder.newTrigger().forJob(jobDetail)
                .startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever()).build();
 
        Trigger trigger2 = TriggerBuilder.newTrigger().forJob(jobDetail)
                .startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(5)).build();
 
        scheduler.scheduleJob(trigger1);
        scheduler.scheduleJob(trigger2);
 
        Thread.sleep(10000);
    }
 
    /**
     * 具体业务实现类，实现Job接口的execute方法即可
     */
    public static class MyJob implements Job {
 
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("调度程序开始              MyJob " + this.getClass().getCanonicalName());
        }
    }
 
 
    /**
     * JobListener实现
     * <p/>
     * JobListener方法执行顺序
     * <p/>
     * 如果TriigerListener的vetoJobExecution返回true
     * triggerFired -> vetoJobExecution ->jobExecutionVetoed
     * <p/>
     * 如果TiggerListener的vetoJobExecution返回false
     * triggerFired -> vetoJobExecution ->jobToBeExecuted -> [Job execute] -> jobWasExecuted
     * ->[triggerMisfired|triggerComplete]
     */
    public static class MyJobListener implements JobListener {
        @Override
        public String getName() {
            return "MyJobListener";
        }
 
        @Override
        public void jobToBeExecuted(JobExecutionContext context) {
            System.out.println("jobToBeExecuted:" + context.getJobDetail().getDescription());
        }
 
        @Override
        public void jobExecutionVetoed(JobExecutionContext context) {
            System.out.println("jobExecutionVetoed:" + context.getJobDetail().getDescription());
        }
 
        @Override
        public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
            System.out.println("jobWasExecuted:" + context.getJobDetail().getDescription());
        }
    }
 
    /**
     * TriggerListener实现
     */
    public static class MyTriggerListener implements TriggerListener {
 
        @Override
        public String getName() {
            return "MyTriggerListener";
        }
 
        @Override
        public void triggerFired(Trigger trigger, JobExecutionContext context) {
            System.out.println("triggerFired:" + trigger.getDescription());
        }
 
        @Override
        public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
            System.out.println("vetoJobExecution:" + trigger.getDescription());
            return false;
 
            /**
             return:false
             triggerFired:MyTrigger
             vetoJobExecution:MyTrigger
             jobToBeExecuted:MyJobDetail
             MyJob secondriver.quartz.TestJobListener.MyJob
             jobWasExecuted:MyJobDetail
             triggerComplete:MyTrigger
             */
            /**
             return:true
             triggerFired:MyTrigger
             vetoJobExecution:MyTrigger
             jobExecutionVetoed:MyJobDetail
             */
        }
 
        @Override
        public void triggerMisfired(Trigger trigger) {
            System.out.println("triggerMisfired:" + trigger.getDescription());
        }
 
        @Override
        public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
            System.out.println("triggerComplete:" + trigger.getDescription());
        }
    }
}
