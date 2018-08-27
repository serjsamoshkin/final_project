package model.service.daemon;


public class DaemonStarter {


    public void start(){

        Thread dt = new Thread(new SendReviewDaemon(), "SendReviewDaemon");
        dt.start();

    }

}
