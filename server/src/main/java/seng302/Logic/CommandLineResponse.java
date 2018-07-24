package seng302.Logic;

public class CommandLineResponse {
    private boolean success;
    private String response;
    private long userId;

    public CommandLineResponse(boolean success, String response){
        this.success = success;
        this.response = response;
    }

    public CommandLineResponse(boolean success, String response, long userId){
        this.success = success;
        this.response = response;
        this.userId = userId;
    }


    public boolean isSuccess(){
        return success;
    }

    public String getResponse(){
        return response;
    }

    public long getUserId() { return  userId; }
}
