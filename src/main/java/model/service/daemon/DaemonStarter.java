package model.service.daemon;


public class DaemonStarter {


    public void Start(){

        Thread dt = new Thread(new SendReviewDaemon(), "SendReviewDaemon");
        dt.setDaemon(true);
        dt.start();

    }

}
